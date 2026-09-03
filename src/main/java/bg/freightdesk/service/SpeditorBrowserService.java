package bg.freightdesk.service;

import bg.freightdesk.entity.FreightRequest;
import bg.freightdesk.repository.FreightRequestRepository;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SpeditorBrowserService {

    private final FreightRequestRepository freightRequestRepository;

    @Value("${speditor.username}")
    private String username;

    @Value("${speditor.password}")
    private String password;

    public SpeditorBrowserService(
            FreightRequestRepository freightRequestRepository) {

        this.freightRequestRepository =
                freightRequestRepository;
    }

    public void openSpeditor(UUID freightRequestId) {

        FreightRequest freightRequest =
                freightRequestRepository.findById(
                        freightRequestId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Freight request not found: "
                                        + freightRequestId
                        )
                );

        Thread thread = new Thread(() -> {

            try (Playwright playwright =
                         Playwright.create()) {

                BrowserContext context =
                        playwright.chromium()
                                .launchPersistentContext(
                                        Paths.get(
                                                "./data/speditor-profile"
                                        ),
                                        new BrowserType
                                                .LaunchPersistentContextOptions()
                                                .setHeadless(false)
                                );

                Page page;

                if (context.pages().isEmpty()) {
                    page = context.newPage();
                } else {
                    page = context.pages().get(0);
                }

                // ==========================================
                // SPEDITOR CARGO FORM
                // ==========================================

                page.navigate(
                        "https://www1.speditor.net/cgi-bin/borsa.pl?act=tovar"
                );

                page.waitForLoadState();

                System.out.println(
                        "================================"
                );

                System.out.println(
                        "Checking existing Speditor session..."
                );

                System.out.println(
                        "Current URL: " + page.url()
                );

                boolean cargoFormAvailable =
                        isCargoFormAvailable(page);

                if (cargoFormAvailable) {

                    System.out.println(
                            "Existing Speditor session found."
                    );

                    System.out.println(
                            "Login skipped."
                    );

                } else {

                    System.out.println(
                            "No active Speditor session found."
                    );

                    System.out.println(
                            "Opening Speditor login page..."
                    );

                    page.navigate(
                            "https://www1.speditor.net/cgi-bin/sl3"
                    );

                    page.waitForLoadState();

                    System.out.println(
                            "Login page URL: "
                                    + page.url()
                    );

                    Locator usernameField =
                            findUsernameField(page);

                    Locator passwordField =
                            findPasswordField(page);

                    usernameField.fill(username);

                    passwordField.fill(password);

                    Locator loginButton =
                            findLoginButton(page);

                    loginButton.click();

                    page.waitForLoadState();

                    System.out.println(
                            "Speditor login submitted."
                    );

                    System.out.println(
                            "URL after login: "
                                    + page.url()
                    );

                    page.navigate(
                            "https://www1.speditor.net/cgi-bin/borsa.pl?act=tovar"
                    );

                    page.waitForLoadState();

                    System.out.println(
                            "Checking cargo form after login..."
                    );

                    if (!isCargoFormAvailable(page)) {

                        throw new IllegalStateException(
                                "Speditor login failed. "
                                        + "Cargo form could not be opened."
                        );
                    }

                    System.out.println(
                            "Speditor login successful."
                    );
                }

                // ==========================================
                // CARGO FORM
                // ==========================================

                System.out.println(
                        "================================"
                );

                System.out.println(
                        "Cargo form opened."
                );

                System.out.println(
                        "URL: " + page.url()
                );

                // ==========================================
                // FROM CITY
                // ==========================================

                findCargoField(
                        page,
                        "fromcity",
                        "град/код"
                ).fill(
                        buildLocation(
                                freightRequest.getLoadingCity(),
                                freightRequest
                                        .getLoadingPostalCode()
                        )
                );

                // ==========================================
                // TO CITY
                // ==========================================

                findCargoField(
                        page,
                        "tocity",
                        "град/код"
                ).fill(
                        buildLocation(
                                freightRequest.getUnloadingCity(),
                                freightRequest
                                        .getUnloadingPostalCode()
                        )
                );

                // ==========================================
                // LOADING COUNTRY
                // ==========================================

                if (freightRequest.getLoadingCountry()
                        != null
                        && !freightRequest
                        .getLoadingCountry()
                        .isBlank()) {

                    findCountrySelect(
                            page,
                            "fromcountry",
                            true
                    ).selectOption(
                            freightRequest
                                    .getLoadingCountry()
                    );
                }

                // ==========================================
                // UNLOADING COUNTRY
                // ==========================================

                if (freightRequest.getUnloadingCountry()
                        != null
                        && !freightRequest
                        .getUnloadingCountry()
                        .isBlank()) {

                    findCountrySelect(
                            page,
                            "tocountry",
                            false
                    ).selectOption(
                            freightRequest
                                    .getUnloadingCountry()
                    );
                }

                // ==========================================
                // LOADING DATE
                // ==========================================

                LocalDate loadingDate =
                        freightRequest.getLoadingDate();

                if (loadingDate != null) {

                    findLoadingDateSelect(
                            page,
                            "day",
                            "wday",
                            0
                    ).selectOption(
                            String.valueOf(
                                    loadingDate
                                            .getDayOfMonth()
                            )
                    );

                    findLoadingDateSelect(
                            page,
                            "mon",
                            "wmon",
                            1
                    ).selectOption(
                            String.valueOf(
                                    loadingDate
                                            .getMonthValue()
                            )
                    );

                    findLoadingDateSelect(
                            page,
                            "year",
                            "wyear",
                            2
                    ).selectOption(
                            String.valueOf(
                                    loadingDate.getYear()
                            )
                    );

                    // ======================================
                    // DELIVERY TIME
                    // ======================================

                    if (freightRequest
                            .getDeliveryTime() != null
                            && !freightRequest
                            .getDeliveryTime()
                            .isBlank()) {

                        findCargoField(
                                page,
                                "sdostavka",
                                "Срок доставка"
                        ).fill(
                                freightRequest
                                        .getDeliveryTime()
                        );
                    }

                    // ======================================
                    // OFFER VALIDITY
                    // ======================================

                    LocalDate validityDate =
                            loadingDate.plusDays(3);

                    findValidityDateSelect(
                            page,
                            "uday",
                            0
                    ).selectOption(
                            String.valueOf(
                                    validityDate
                                            .getDayOfMonth()
                            )
                    );

                    findValidityDateSelect(
                            page,
                            "umon",
                            1
                    ).selectOption(
                            String.valueOf(
                                    validityDate
                                            .getMonthValue()
                            )
                    );

                    findValidityDateSelect(
                            page,
                            "uyear",
                            2
                    ).selectOption(
                            String.valueOf(
                                    validityDate.getYear()
                            )
                    );
                }

                // ==========================================
                // WEIGHT
                // ==========================================

                if (freightRequest.getWeight() != null) {

                    findCargoField(
                            page,
                            "bteglo",
                            "Бруто тегло"
                    ).fill(
                            freightRequest
                                    .getWeight()
                                    .toPlainString()
                    );
                }

                // ==========================================
                // VOLUME
                // ==========================================

                if (freightRequest.getVolume() != null) {

                    findCargoField(
                            page,
                            "obem",
                            "Обем"
                    ).fill(
                            freightRequest
                                    .getVolume()
                                    .toPlainString()
                    );
                }

                // ==========================================
                // DIMENSIONS
                // ==========================================

                if (freightRequest.getDimensions() != null
                        && !freightRequest
                        .getDimensions()
                        .isBlank()) {

                    findCargoField(
                            page,
                            "razmeri",
                            "Размери"
                    ).fill(
                            freightRequest
                                    .getDimensions()
                    );
                }

                // ==========================================
                // LOADING METERS
                // ==========================================

                if (freightRequest.getLoadingMeters()
                        != null) {

                    findCargoField(
                            page,
                            "lmetri",
                            "Линейни метри"
                    ).fill(
                            freightRequest
                                    .getLoadingMeters()
                                    .toPlainString()
                    );
                }

                // ==========================================
                // PALLETS / CARGO TYPE DESCRIPTION
                // ==========================================

                if (freightRequest.getPallets()
                        != null) {

                    findCargoField(
                            page,
                            "vtovar",
                            "Вид на товара"
                    ).fill(
                            freightRequest.getPallets()
                                    + " pallets"
                    );
                }

                // ==========================================
                // CARGO TYPE
                // ==========================================

                if (freightRequest.getCargoType()
                        != null
                        && !freightRequest
                        .getCargoType()
                        .isBlank()) {

                    findCargoSelect(
                            page,
                            "tiptovar",
                            "Тип на товара"
                    ).selectOption(
                            freightRequest
                                    .getCargoType()
                    );
                }

                // ==========================================
                // SPEDITOR PRICE
                // ==========================================

                if (freightRequest.getSpeditorPrice()
                        != null
                        && !freightRequest
                        .getSpeditorPrice()
                        .isBlank()) {

                    findCargoField(
                            page,
                            "ceni",
                            "Цена"
                    ).fill(
                            freightRequest
                                    .getSpeditorPrice()
                    );
                }

                // ==========================================
                // DRUGI
                // ==========================================

                String drugi =
                        buildDrugi(freightRequest);

                if (!drugi.isBlank()) {

                    findCargoField(
                            page,
                            "drugi",
                            "Други"
                    ).fill(drugi);
                }

                // ==========================================
                // RESULT
                // ==========================================

                System.out.println(
                        "================================"
                );

                System.out.println(
                        "FreightRequest prepared:"
                );

                System.out.println(
                        "ID: "
                                + freightRequest.getId()
                );

                System.out.println(
                        "From: "
                                + freightRequest
                                .getLoadingCountry()
                                + " "
                                + freightRequest
                                .getLoadingCity()
                );

                System.out.println(
                        "To: "
                                + freightRequest
                                .getUnloadingCountry()
                                + " "
                                + freightRequest
                                .getUnloadingCity()
                );

                System.out.println(
                        "Loading date: "
                                + freightRequest
                                .getLoadingDate()
                );

                System.out.println(
                        "Delivery time: "
                                + freightRequest
                                .getDeliveryTime()
                );

                System.out.println(
                        "Weight: "
                                + freightRequest
                                .getWeight()
                );

                System.out.println(
                        "Volume: "
                                + freightRequest
                                .getVolume()
                );

                System.out.println(
                        "Dimensions: "
                                + freightRequest
                                .getDimensions()
                );

                System.out.println(
                        "Loading meters: "
                                + freightRequest
                                .getLoadingMeters()
                );

                System.out.println(
                        "Pallets: "
                                + freightRequest
                                .getPallets()
                );

                System.out.println(
                        "Speditor price: "
                                + freightRequest
                                .getSpeditorPrice()
                );

                System.out.println(
                        "Drugi: "
                                + drugi
                );

                System.out.println(
                        "================================"
                );

                System.out.println(
                        "Speditor form is ready."
                );

                System.out.println(
                        "DO NOT SUBMIT AUTOMATICALLY."
                );

                // ==========================================
                // KEEP BROWSER OPEN
                // ==========================================

                while (!page.isClosed()) {
                    Thread.sleep(1000);
                }

                context.close();

            } catch (Exception e) {

                System.err.println(
                        "Error while opening Speditor:"
                );

                e.printStackTrace();
            }

        });

        thread.setName(
                "speditor-" + freightRequestId
        );

        thread.start();
    }

    // =====================================================
    // BUILD DRUGI
    // =====================================================

    private String buildDrugi(
            FreightRequest freightRequest) {

        List<String> options =
                new ArrayList<>();

        if (freightRequest.isTrailer()) {
            options.add("Прицеп");
        }

        if (freightRequest.isHanger()) {
            options.add("Хенгер");
        }

        if (freightRequest.isRefrigerated()) {
            options.add("Хладилен");
        }

        if (freightRequest.isDualTemperature()) {
            options.add("Двутемпературен");
        }

        if (freightRequest.isVan()) {
            options.add("Фургон");
        }

        if (freightRequest.isMicrobus()) {
            options.add("Микробус");
        }

        if (freightRequest.isClothingTruck()) {
            options.add("Дрехарка");
        }

        if (freightRequest.isSolo()) {
            options.add("Соло");
        }

        if (freightRequest.isJumbo()) {
            options.add("Джъмбо");
        }

        if (freightRequest.isTractor()) {
            options.add("Влекач");
        }

        if (freightRequest.isPlatform()) {
            options.add("Платформа");
        }

        if (freightRequest.isFlatbed()) {
            options.add("Бордови");
        }

        if (freightRequest.isContainerTruck()) {
            options.add("Контейнеровоз");
        }

        if (freightRequest.isContainer()) {
            options.add("Контейнер");
        }

        if (freightRequest.isTanker()) {
            options.add("Цистерна");
        }

        if (freightRequest.isMegatrailer()) {
            options.add("Мегатрейлър");
        }

        if (freightRequest.isCarCarrier()) {
            options.add("Автовоз");
        }

        if (freightRequest.isGondola()) {
            options.add("Гондола");
        }

        if (freightRequest.isOversized()) {
            options.add("Извънгабаритен");
        }

        if (freightRequest.isTrailerOnly()) {
            options.add("Ремарке");
        }

        if (freightRequest.isTunnel()) {
            options.add("С тунел");
        }

        if (freightRequest.isTailLift()) {
            options.add("Падащ борд");
        }

        if (freightRequest.isCrane()) {
            options.add("Кран");
        }

        if (freightRequest.isTwoDrivers()) {
            options.add("Двойка шофьори");
        }

        if (freightRequest.isPalletTruck()) {
            options.add("Палетна количка");
        }

        StringBuilder result =
                new StringBuilder();

        // ==========================================
        // FREE TEXT
        // ==========================================

        if (freightRequest.getAdditionalInfo()
                != null
                && !freightRequest
                .getAdditionalInfo()
                .isBlank()) {

            result.append(
                    freightRequest.getAdditionalInfo()
            );
        }

        // ==========================================
        // VEHICLE OPTIONS
        // ==========================================

        if (!options.isEmpty()) {

            if (result.length() > 0) {

                result.append(" ");
            }

            result.append("за ");

            for (int i = 0;
                 i < options.size();
                 i++) {

                if (i > 0) {

                    String previous =
                            options.get(i - 1);

                    if ("Ремарке".equals(previous)) {
                        result.append(" + ");
                    } else {
                        result.append(" / ");
                    }
                }

                result.append(
                        options.get(i)
                );
            }
        }

        return result.toString();
    }

    // =====================================================
    // CARGO FORM AVAILABLE
    // =====================================================

    private boolean isCargoFormAvailable(
            Page page) {

        Locator fromCity =
                page.locator(
                        "input[name='fromcity']"
                );

        boolean available =
                fromCity.count() > 0;

        System.out.println(
                "Cargo form available: "
                        + available
        );

        return available;
    }

    // =====================================================
    // CARGO FIELD FINDER
    // =====================================================

    private Locator findCargoField(
            Page page,
            String fieldName,
            String labelText) {

        String selector =
                "input[name='" + fieldName + "']";

        Locator locator =
                page.locator(selector);

        if (locator.count() > 0) {

            System.out.println(
                    "Cargo field found: "
                            + fieldName
                            + " -> "
                            + selector
            );

            return locator.first();
        }

        selector =
                "textarea[name='" + fieldName + "']";

        locator =
                page.locator(selector);

        if (locator.count() > 0) {

            System.out.println(
                    "Cargo field found: "
                            + fieldName
                            + " -> "
                            + selector
            );

            return locator.first();
        }

        selector =
                "#" + fieldName;

        locator =
                page.locator(selector);

        if (locator.count() > 0) {

            System.out.println(
                    "Cargo field found: "
                            + fieldName
                            + " -> "
                            + selector
            );

            return locator.first();
        }

        if (labelText != null
                && !labelText.isBlank()) {

            Locator rows =
                    page.locator("tr");

            int rowCount =
                    rows.count();

            for (int i = 0;
                 i < rowCount;
                 i++) {

                Locator row =
                        rows.nth(i);

                Locator header =
                        row.locator("th");

                if (header.count() == 0) {
                    continue;
                }

                String text =
                        header.first().innerText();

                if (text == null) {
                    continue;
                }

                if (!normalizeText(text)
                        .contains(
                                normalizeText(labelText)
                        )) {
                    continue;
                }

                Locator inputs =
                        row.locator(
                                "input:not([type='hidden'])"
                        );

                if (inputs.count() > 0) {

                    System.out.println(
                            "Cargo field found by label: "
                                    + fieldName
                                    + " -> "
                                    + labelText
                    );

                    return inputs.first();
                }

                Locator textareas =
                        row.locator("textarea");

                if (textareas.count() > 0) {

                    System.out.println(
                            "Cargo field found by label: "
                                    + fieldName
                                    + " -> "
                                    + labelText
                    );

                    return textareas.first();
                }
            }
        }

        throw new IllegalStateException(
                "Could not find Speditor cargo field: "
                        + fieldName
                        + " / label: "
                        + labelText
        );
    }

    // =====================================================
    // SELECT FINDER
    // =====================================================

    private Locator findCargoSelect(
            Page page,
            String fieldName,
            String labelText) {

        String selector =
                "select[name='" + fieldName + "']";

        Locator locator =
                page.locator(selector);

        if (locator.count() > 0) {

            System.out.println(
                    "Cargo select found: "
                            + fieldName
                            + " -> "
                            + selector
            );

            return locator.first();
        }

        selector =
                "#" + fieldName;

        locator =
                page.locator(selector);

        if (locator.count() > 0) {

            System.out.println(
                    "Cargo select found: "
                            + fieldName
                            + " -> "
                            + selector
            );

            return locator.first();
        }

        if (labelText != null
                && !labelText.isBlank()) {

            Locator rows =
                    page.locator("tr");

            int rowCount =
                    rows.count();

            for (int i = 0;
                 i < rowCount;
                 i++) {

                Locator row =
                        rows.nth(i);

                Locator header =
                        row.locator("th");

                if (header.count() == 0) {
                    continue;
                }

                String text =
                        header.first().innerText();

                if (text == null) {
                    continue;
                }

                if (!normalizeText(text)
                        .contains(
                                normalizeText(labelText)
                        )) {
                    continue;
                }

                Locator selects =
                        row.locator("select");

                if (selects.count() > 0) {

                    System.out.println(
                            "Cargo select found by label: "
                                    + fieldName
                                    + " -> "
                                    + labelText
                    );

                    return selects.first();
                }
            }
        }

        throw new IllegalStateException(
                "Could not find Speditor cargo select: "
                        + fieldName
                        + " / label: "
                        + labelText
        );
    }

    // =====================================================
    // COUNTRY SELECT
    // =====================================================

    private Locator findCountrySelect(
            Page page,
            String fieldName,
            boolean loadingCountry) {

        String selector =
                "select[name='" + fieldName + "']";

        Locator locator =
                page.locator(selector);

        if (locator.count() > 0) {

            System.out.println(
                    "Country select found: "
                            + fieldName
                            + " -> "
                            + selector
            );

            return locator.first();
        }

        selector =
                "#" + fieldName;

        locator =
                page.locator(selector);

        if (locator.count() > 0) {

            System.out.println(
                    "Country select found: "
                            + fieldName
                            + " -> "
                            + selector
            );

            return locator.first();
        }

        Locator rows =
                page.locator("tr");

        for (int i = 0;
             i < rows.count();
             i++) {

            Locator row =
                    rows.nth(i);

            Locator header =
                    row.locator("th");

            if (header.count() == 0) {
                continue;
            }

            String text =
                    header.first().innerText();

            if (text == null) {
                continue;
            }

            if (!normalizeText(text)
                    .contains("релация")) {
                continue;
            }

            Locator selects =
                    row.locator("select");

            if (selects.count() < 2) {
                continue;
            }

            if (loadingCountry) {

                System.out.println(
                        "Country select found by relation row: "
                                + fieldName
                                + " -> first select"
                );

                return selects.nth(0);

            } else {

                System.out.println(
                        "Country select found by relation row: "
                                + fieldName
                                + " -> second select"
                );

                return selects.nth(1);
            }
        }

        throw new IllegalStateException(
                "Could not find Speditor country select: "
                        + fieldName
        );
    }

    // =====================================================
    // LOADING DATE SELECT
    // =====================================================

    private Locator findLoadingDateSelect(
            Page page,
            String fieldName,
            String id,
            int fallbackIndex) {

        String selector =
                "select[name='" + fieldName + "']";

        Locator locator =
                page.locator(selector);

        if (locator.count() > 0) {

            System.out.println(
                    "Loading date select found: "
                            + fieldName
                            + " -> "
                            + selector
            );

            return locator.first();
        }

        locator =
                page.locator("#" + id);

        if (locator.count() > 0) {

            System.out.println(
                    "Loading date select found: "
                            + fieldName
                            + " -> #"
                            + id
            );

            return locator.first();
        }

        Locator rows =
                page.locator("tr");

        for (int i = 0;
             i < rows.count();
             i++) {

            Locator row =
                    rows.nth(i);

            Locator header =
                    row.locator("th");

            if (header.count() == 0) {
                continue;
            }

            String text =
                    header.first().innerText();

            if (text == null) {
                continue;
            }

            if (!normalizeText(text)
                    .contains(
                            "дата на товарене"
                    )) {
                continue;
            }

            Locator selects =
                    row.locator("select");

            if (selects.count()
                    > fallbackIndex) {

                System.out.println(
                        "Loading date select found by label: "
                                + fieldName
                );

                return selects.nth(
                        fallbackIndex
                );
            }
        }

        throw new IllegalStateException(
                "Could not find Speditor loading date select: "
                        + fieldName
        );
    }

    // =====================================================
    // VALIDITY DATE SELECT
    // =====================================================

    private Locator findValidityDateSelect(
            Page page,
            String fieldName,
            int fallbackIndex) {

        String selector =
                "select[name='" + fieldName + "']";

        Locator locator =
                page.locator(selector);

        if (locator.count() > 0) {

            System.out.println(
                    "Validity date select found: "
                            + fieldName
                            + " -> "
                            + selector
            );

            return locator.first();
        }

        Locator rows =
                page.locator("tr");

        for (int i = 0;
             i < rows.count();
             i++) {

            Locator row =
                    rows.nth(i);

            String text =
                    row.innerText();

            if (text == null) {
                continue;
            }

            if (!normalizeText(text)
                    .contains(
                            "валидност до"
                    )) {
                continue;
            }

            Locator selects =
                    row.locator("select");

            int index =
                    3 + fallbackIndex;

            if (selects.count() > index) {

                System.out.println(
                        "Validity date select found by label: "
                                + fieldName
                );

                return selects.nth(index);
            }
        }

        throw new IllegalStateException(
                "Could not find Speditor validity date select: "
                        + fieldName
        );
    }

    // =====================================================
    // NORMALIZE TEXT
    // =====================================================

    private String normalizeText(
            String text) {

        return text
                .replace('\u00A0', ' ')
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase();
    }

    // =====================================================
    // USERNAME
    // =====================================================

    private Locator findUsernameField(
            Page page) {

        String[] selectors = {
                "input[name='user']",
                "input[name='username']",
                "input[id='user']",
                "input[id='username']"
        };

        for (String selector : selectors) {

            Locator locator =
                    page.locator(selector);

            if (locator.count() > 0) {

                System.out.println(
                        "Username field found: "
                                + selector
                );

                return locator.first();
            }
        }

        throw new IllegalStateException(
                "Could not find Speditor username field."
        );
    }

    // =====================================================
    // PASSWORD
    // =====================================================

    private Locator findPasswordField(
            Page page) {

        String[] selectors = {
                "input[name='pass']",
                "input[name='password']",
                "input[id='pass']",
                "input[id='password']",
                "input[type='password']"
        };

        for (String selector : selectors) {

            Locator locator =
                    page.locator(selector);

            if (locator.count() > 0) {

                System.out.println(
                        "Password field found: "
                                + selector
                );

                return locator.first();
            }
        }

        throw new IllegalStateException(
                "Could not find Speditor password field."
        );
    }

    // =====================================================
    // LOGIN BUTTON
    // =====================================================

    private Locator findLoginButton(
            Page page) {

        String[] selectors = {
                "input[type='submit']",
                "button[type='submit']"
        };

        for (String selector : selectors) {

            Locator locator =
                    page.locator(selector);

            if (locator.count() > 0) {

                System.out.println(
                        "Login button found: "
                                + selector
                );

                return locator.first();
            }
        }

        throw new IllegalStateException(
                "Could not find Speditor login button."
        );
    }

    // =====================================================
    // BUILD LOCATION
    // =====================================================

    private String buildLocation(
            String city,
            String postalCode) {

        if (city == null) {

            return postalCode != null
                    ? postalCode
                    : "";
        }

        if (postalCode == null
                || postalCode.isBlank()) {

            return city;
        }

        return postalCode
                + " "
                + city;
    }
}