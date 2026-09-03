package bg.freightdesk;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpeditorSelectOptionsTest {

    private static final Charset SPEDITOR_CHARSET =
            Charset.forName("windows-1251");

    public static void main(String[] args) throws Exception {

        System.out.println("================================");
        System.out.println("SPEDITOR FORM ANALYSIS TEST");
        System.out.println("================================");


        // ============================================================
        // 1. USER DATA
        // ============================================================

        String username = "Т.Радев";
        String password = "Tradev3233";


        // ============================================================
        // 2. COOKIE MANAGER
        // ============================================================

        CookieManager cookieManager =
                new CookieManager();

        cookieManager.setCookiePolicy(
                CookiePolicy.ACCEPT_ALL
        );


        // ============================================================
        // 3. HTTP CLIENT
        // ============================================================

        HttpClient client =
                HttpClient.newBuilder()
                        .cookieHandler(cookieManager)
                        .followRedirects(
                                HttpClient.Redirect.NORMAL
                        )
                        .build();


        // ============================================================
        // 4. LOGIN URL
        // ============================================================

        String loginUrl =
                "https://www1.speditor.net/cgi-bin/sl3";


        // ============================================================
        // 5. OPEN LOGIN PAGE
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("1. OPEN LOGIN PAGE");
        System.out.println("================================");

        HttpRequest loginPageRequest =
                HttpRequest.newBuilder()
                        .uri(URI.create(loginUrl))
                        .header(
                                "User-Agent",
                                "Mozilla/5.0"
                        )
                        .header(
                                "Accept",
                                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
                        )
                        .header(
                                "Accept-Language",
                                "bg-BG,bg;q=0.9,en-US;q=0.8,en;q=0.7"
                        )
                        .GET()
                        .build();


        HttpResponse<String> loginPageResponse =
                client.send(
                        loginPageRequest,
                        HttpResponse.BodyHandlers.ofString(
                                SPEDITOR_CHARSET
                        )
                );


        System.out.println(
                "GET STATUS: "
                        + loginPageResponse.statusCode()
        );


        // ============================================================
        // 6. LOGIN
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("2. LOGIN");
        System.out.println("================================");

        String loginData =
                "w1=1" +
                        "&user=" + encode(username) +
                        "&pass=" + encode(password);


        HttpRequest loginRequest =
                HttpRequest.newBuilder()
                        .uri(URI.create(loginUrl))
                        .header(
                                "Content-Type",
                                "application/x-www-form-urlencoded"
                        )
                        .header(
                                "User-Agent",
                                "Mozilla/5.0"
                        )
                        .header(
                                "Accept",
                                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
                        )
                        .header(
                                "Accept-Language",
                                "bg-BG,bg;q=0.9,en-US;q=0.8,en;q=0.7"
                        )
                        .header(
                                "Referer",
                                loginUrl
                        )
                        .header(
                                "Origin",
                                "https://www1.speditor.net"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        loginData,
                                        SPEDITOR_CHARSET
                                )
                        )
                        .build();


        HttpResponse<String> loginResponse =
                client.send(
                        loginRequest,
                        HttpResponse.BodyHandlers.ofString(
                                SPEDITOR_CHARSET
                        )
                );


        System.out.println(
                "LOGIN STATUS: "
                        + loginResponse.statusCode()
        );


        // ============================================================
        // 7. SESSION CHECK
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("3. SESSION CHECK");
        System.out.println("================================");

        boolean hasSession =
                cookieManager
                        .getCookieStore()
                        .getCookies()
                        .stream()
                        .anyMatch(cookie ->
                                cookie.getName()
                                        .equalsIgnoreCase("sess")
                        );


        if (!hasSession) {

            System.out.println(
                    "ERROR: sess COOKIE NOT FOUND."
            );

            System.out.println(
                    "LOGIN FAILED."
            );

            return;
        }


        System.out.println(
                "SUCCESS: sess COOKIE FOUND."
        );


        // ============================================================
        // 8. OPEN CARGO FORM
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("4. OPEN CARGO FORM");
        System.out.println("================================");


        String formUrl =
                "https://www1.speditor.net/cgi-bin/borsa.pl" +
                        "?act=tovar" +
                        "&tID=1788244045014590";


        HttpRequest formRequest =
                HttpRequest.newBuilder()
                        .uri(URI.create(formUrl))
                        .header(
                                "User-Agent",
                                "Mozilla/5.0"
                        )
                        .header(
                                "Accept",
                                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
                        )
                        .header(
                                "Accept-Language",
                                "bg-BG,bg;q=0.9,en-US;q=0.8,en;q=0.7"
                        )
                        .header(
                                "Referer",
                                loginUrl
                        )
                        .GET()
                        .build();


        HttpResponse<String> formResponse =
                client.send(
                        formRequest,
                        HttpResponse.BodyHandlers.ofString(
                                SPEDITOR_CHARSET
                        )
                );


        System.out.println(
                "STATUS: "
                        + formResponse.statusCode()
        );

        System.out.println(
                "FINAL URL: "
                        + formResponse.uri()
        );

        System.out.println(
                "RESPONSE SIZE: "
                        + formResponse.body().length()
                        + " characters"
        );


        String html =
                formResponse.body();


        // ============================================================
        // 9. FIND ALL FORMS
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("5. FORMS");
        System.out.println("================================");


        Pattern formPattern =
                Pattern.compile(
                        "<form\\b([^>]*)>(.*?)</form\\s*>",
                        Pattern.CASE_INSENSITIVE
                                | Pattern.DOTALL
                );


        Matcher formMatcher =
                formPattern.matcher(html);


        int formCount = 0;


        while (formMatcher.find()) {

            formCount++;


            String formAttributes =
                    formMatcher.group(1);

            String formBody =
                    formMatcher.group(2);


            System.out.println();
            System.out.println("--------------------------------");
            System.out.println(
                    "FORM #" + formCount
            );
            System.out.println("--------------------------------");


            // ========================================================
            // FORM ACTION
            // ========================================================

            String action =
                    extractAttribute(
                            formAttributes,
                            "action"
                    );


            // ========================================================
            // FORM METHOD
            // ========================================================

            String method =
                    extractAttribute(
                            formAttributes,
                            "method"
                    );


            // ========================================================
            // FORM NAME
            // ========================================================

            String formName =
                    extractAttribute(
                            formAttributes,
                            "name"
                    );


            // ========================================================
            // FORM ID
            // ========================================================

            String formId =
                    extractAttribute(
                            formAttributes,
                            "id"
                    );


            System.out.println(
                    "action = "
                            + (
                            action.isEmpty()
                                    ? "[none]"
                                    : action
                    )
            );


            System.out.println(
                    "method = "
                            + (
                            method.isEmpty()
                                    ? "[none]"
                                    : method
                    )
            );


            System.out.println(
                    "name = "
                            + (
                            formName.isEmpty()
                                    ? "[none]"
                                    : formName
                    )
            );


            System.out.println(
                    "id = "
                            + (
                            formId.isEmpty()
                                    ? "[none]"
                                    : formId
                    )
            );


            // ========================================================
            // 10. INPUTS
            // ========================================================

            System.out.println();
            System.out.println("INPUTS");
            System.out.println("--------------------------------");


            Pattern inputPattern =
                    Pattern.compile(
                            "<input\\b([^>]*)>",
                            Pattern.CASE_INSENSITIVE
                    );


            Matcher inputMatcher =
                    inputPattern.matcher(formBody);


            int inputCount = 0;


            while (inputMatcher.find()) {

                inputCount++;


                String attributes =
                        inputMatcher.group(1);


                String name =
                        extractAttribute(
                                attributes,
                                "name"
                        );


                String type =
                        extractAttribute(
                                attributes,
                                "type"
                        );


                String value =
                        extractAttribute(
                                attributes,
                                "value"
                        );


                String id =
                        extractAttribute(
                                attributes,
                                "id"
                        );


                System.out.println();

                System.out.println(
                        "INPUT #" + inputCount
                );

                System.out.println(
                        "type  = "
                                + (
                                type.isEmpty()
                                        ? "[none]"
                                        : type
                        )
                );

                System.out.println(
                        "name  = "
                                + (
                                name.isEmpty()
                                        ? "[none]"
                                        : name
                        )
                );

                System.out.println(
                        "value = "
                                + (
                                value.isEmpty()
                                        ? "[none]"
                                        : value
                        )
                );

                System.out.println(
                        "id    = "
                                + (
                                id.isEmpty()
                                        ? "[none]"
                                        : id
                        )
                );
            }


            System.out.println();
            System.out.println(
                    "TOTAL INPUTS: "
                            + inputCount
            );


            // ========================================================
            // 11. TEXTAREAS
            // ========================================================

            System.out.println();
            System.out.println("TEXTAREAS");
            System.out.println("--------------------------------");


            Pattern textareaPattern =
                    Pattern.compile(
                            "<textarea\\b([^>]*)>(.*?)</textarea\\s*>",
                            Pattern.CASE_INSENSITIVE
                                    | Pattern.DOTALL
                    );


            Matcher textareaMatcher =
                    textareaPattern.matcher(formBody);


            int textareaCount = 0;


            while (textareaMatcher.find()) {

                textareaCount++;


                String attributes =
                        textareaMatcher.group(1);


                String textareaValue =
                        textareaMatcher.group(2);


                String name =
                        extractAttribute(
                                attributes,
                                "name"
                        );


                String id =
                        extractAttribute(
                                attributes,
                                "id"
                        );


                textareaValue =
                        cleanText(textareaValue);


                System.out.println();

                System.out.println(
                        "TEXTAREA #" + textareaCount
                );

                System.out.println(
                        "name = "
                                + (
                                name.isEmpty()
                                        ? "[none]"
                                        : name
                        )
                );

                System.out.println(
                        "id = "
                                + (
                                id.isEmpty()
                                        ? "[none]"
                                        : id
                        )
                );

                System.out.println(
                        "value = "
                                + (
                                textareaValue.isEmpty()
                                        ? "[empty]"
                                        : textareaValue
                        )
                );
            }


            System.out.println();
            System.out.println(
                    "TOTAL TEXTAREAS: "
                            + textareaCount
            );


            // ========================================================
// 12. SELECTS
// ========================================================

            System.out.println();
            System.out.println("SELECTS");
            System.out.println("--------------------------------");

            Pattern selectPattern =
                    Pattern.compile(
                            "<select\\b([^>]*)>(.*?)</select\\s*>",
                            Pattern.CASE_INSENSITIVE
                                    | Pattern.DOTALL
                    );

            Matcher selectMatcher =
                    selectPattern.matcher(formBody);

            int selectCount = 0;

            while (selectMatcher.find()) {

                selectCount++;

                String selectAttributes =
                        selectMatcher.group(1);

                String selectBody =
                        selectMatcher.group(2);

                String selectName =
                        extractAttribute(
                                selectAttributes,
                                "name"
                        );

                String selectId =
                        extractAttribute(
                                selectAttributes,
                                "id"
                        );

                System.out.println();
                System.out.println("--------------------------------");
                System.out.println(
                        "SELECT #" + selectCount
                );
                System.out.println("--------------------------------");

                System.out.println(
                        "name = "
                                + (
                                selectName.isEmpty()
                                        ? "[none]"
                                        : selectName
                        )
                );

                System.out.println(
                        "id = "
                                + (
                                selectId.isEmpty()
                                        ? "[none]"
                                        : selectId
                        )
                );

                // ====================================================
                // OPTIONS
                // ====================================================

                Pattern optionPattern =
                        Pattern.compile(
                                "<option\\b([^>]*)>(.*?)(?=<option\\b|$)",
                                Pattern.CASE_INSENSITIVE
                                        | Pattern.DOTALL
                        );

                Matcher optionMatcher =
                        optionPattern.matcher(selectBody);

                int optionCount = 0;

                while (optionMatcher.find()) {

                    optionCount++;

                    String optionAttributes =
                            optionMatcher.group(1);

                    String optionText =
                            optionMatcher.group(2);

                    String optionValue =
                            extractAttribute(
                                    optionAttributes,
                                    "value"
                            );

                    optionText =
                            cleanText(optionText);

                    System.out.println();

                    System.out.println(
                            "OPTION #" + optionCount
                    );

                    System.out.println(
                            "value = "
                                    + (
                                    optionValue.isEmpty()
                                            ? "[none]"
                                            : optionValue
                            )
                    );

                    System.out.println(
                            "text = "
                                    + (
                                    optionText.isEmpty()
                                            ? "[empty]"
                                            : optionText
                            )
                    );
                }

                System.out.println();

                System.out.println(
                        "TOTAL OPTIONS: "
                                + optionCount
                );
            }

            System.out.println();

            System.out.println(
                    "TOTAL SELECTS: "
                            + selectCount
            );

            // ========================================================
            // 13. RAW FORM HTML
            // ========================================================

            System.out.println();
            System.out.println("================================");
            System.out.println(
                    "RAW HTML - FORM #" + formCount
            );
            System.out.println("================================");

            System.out.println(
                    "<form"
                            + formAttributes
                            + ">"
            );

            System.out.println(formBody);

            System.out.println("</form>");
        }


        // ============================================================
        // 14. TOTAL FORMS
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("6. TOTAL FORMS");
        System.out.println("================================");

        System.out.println(
                "TOTAL FORMS: "
                        + formCount
        );


        // ============================================================
        // 15. SAFETY CHECK
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("7. SAFETY CHECK");
        System.out.println("================================");

        System.out.println("LOGIN: YES");
        System.out.println("SESSION: YES");
        System.out.println("CARGO FORM: GET ONLY");
        System.out.println("FORM ANALYSIS: READ ONLY");
        System.out.println("CARGO POST: NO");
        System.out.println("ADVERTISEMENT PUBLISHED: NO");


        System.out.println();
        System.out.println("================================");
        System.out.println("SAFE FORM ANALYSIS FINISHED");
        System.out.println("================================");
    }


    // ================================================================
    // EXTRACT ATTRIBUTE
    // ================================================================

    private static String extractAttribute(
            String tag,
            String attributeName
    ) {

        Pattern pattern =
                Pattern.compile(
                        "\\b"
                                + Pattern.quote(attributeName)
                                + "\\s*=\\s*"
                                + "(?:\"([^\"]*)\"|'([^']*)'|([^\\s>]+))",
                        Pattern.CASE_INSENSITIVE
                );


        Matcher matcher =
                pattern.matcher(tag);


        if (!matcher.find()) {

            return "";
        }


        if (matcher.group(1) != null) {

            return matcher.group(1);
        }


        if (matcher.group(2) != null) {

            return matcher.group(2);
        }


        return matcher.group(3);
    }


    // ================================================================
    // CLEAN HTML TEXT
    // ================================================================

    private static String cleanText(
            String text
    ) {

        text =
                text.replaceAll(
                        "<[^>]*>",
                        ""
                );


        text =
                text.replace(
                        "&nbsp;",
                        " "
                );


        text =
                text.replace(
                        "&amp;",
                        "&"
                );


        text =
                text.replace(
                        "&quot;",
                        "\""
                );


        text =
                text.replace(
                        "&#39;",
                        "'"
                );


        return text
                .replaceAll(
                        "\\s+",
                        " "
                )
                .trim();
    }


    // ================================================================
    // URL ENCODING
    // ================================================================

    private static String encode(
            String value
    ) {

        return URLEncoder.encode(
                value,
                SPEDITOR_CHARSET
        );
    }
}