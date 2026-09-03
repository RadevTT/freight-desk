package bg.freightdesk;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.List;

public class SpeditorPostTest {

    private static final String LOGIN_URL =
            "https://www1.speditor.net/cgi-bin/sl3";

    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println("SPEDITOR LOGIN DIAGNOSTIC");
        System.out.println("================================");

        try {

            // =====================================================
            // COOKIE MANAGER
            // =====================================================

            CookieManager cookieManager =
                    new CookieManager(
                            null,
                            CookiePolicy.ACCEPT_ALL
                    );

            // =====================================================
            // HTTP CLIENT
            // =====================================================

            HttpClient client =
                    HttpClient.newBuilder()
                            .cookieHandler(cookieManager)
                            .followRedirects(
                                    HttpClient.Redirect.NORMAL
                            )
                            .build();


            // =====================================================
            // 1. OPEN LOGIN PAGE
            // =====================================================

            System.out.println();
            System.out.println("================================");
            System.out.println("1. OPEN LOGIN PAGE");
            System.out.println("================================");

            HttpRequest getLogin =
                    HttpRequest.newBuilder()
                            .uri(URI.create(LOGIN_URL))
                            .GET()
                            .header(
                                    "User-Agent",
                                    "Mozilla/5.0"
                            )
                            .build();

            HttpResponse<byte[]> getResponse =
                    client.send(
                            getLogin,
                            HttpResponse.BodyHandlers.ofByteArray()
                    );

            System.out.println(
                    "GET STATUS: "
                            + getResponse.statusCode()
            );

            System.out.println(
                    "GET URL: "
                            + getResponse.uri()
            );


            // =====================================================
            // 2. COOKIES AFTER GET
            // =====================================================

            printCookies(
                    cookieManager,
                    "COOKIES AFTER GET"
            );


            // =====================================================
            // 3. LOGIN
            // =====================================================

            System.out.println();
            System.out.println("================================");
            System.out.println("3. LOGIN");
            System.out.println("================================");

            String username =
                    "ТВОЕТО_ПОТРЕБИТЕЛСКО_ИМЕ";

            String password =
                    "ТВОЯТА_ПАРОЛА";


            String postData =
                    "w1=1"
                            + "&user="
                            + urlEncode(username)
                            + "&pass="
                            + urlEncode(password);


            System.out.println(
                    "POST DATA FIELDS: w1, user, pass"
            );


            HttpRequest loginRequest =
                    HttpRequest.newBuilder()
                            .uri(URI.create(LOGIN_URL))
                            .header(
                                    "User-Agent",
                                    "Mozilla/5.0"
                            )
                            .header(
                                    "Content-Type",
                                    "application/x-www-form-urlencoded"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(
                                                    postData,
                                                    Charset.forName(
                                                            "Windows-1251"
                                                    )
                                            )
                            )
                            .build();


            HttpResponse<byte[]> loginResponse =
                    client.send(
                            loginRequest,
                            HttpResponse.BodyHandlers.ofByteArray()
                    );


            System.out.println(
                    "LOGIN STATUS: "
                            + loginResponse.statusCode()
            );

            System.out.println(
                    "LOGIN FINAL URL: "
                            + loginResponse.uri()
            );


            // =====================================================
            // 4. RESPONSE HEADERS
            // =====================================================

            System.out.println();
            System.out.println("================================");
            System.out.println("4. LOGIN RESPONSE HEADERS");
            System.out.println("================================");

            loginResponse.headers()
                    .map()
                    .forEach((name, values) -> {

                        if (name.equalsIgnoreCase("set-cookie")) {

                            for (String value : values) {

                                System.out.println(
                                        "Set-Cookie: "
                                                + value
                                );
                            }
                        }
                    });


            // =====================================================
            // 5. COOKIES AFTER LOGIN
            // =====================================================

            printCookies(
                    cookieManager,
                    "COOKIES AFTER LOGIN"
            );


            // =====================================================
            // 6. FIND SESSION
            // =====================================================

            System.out.println();
            System.out.println("================================");
            System.out.println("6. SESSION CHECK");
            System.out.println("================================");

            String sess = findCookie(
                    cookieManager,
                    "sess"
            );

            String speditorLogin = findCookie(
                    cookieManager,
                    "SpeditorLogin"
            );


            if (sess == null) {

                System.out.println(
                        "ERROR: sess COOKIE NOT FOUND."
                );

            } else {

                System.out.println(
                        "SUCCESS: sess COOKIE FOUND."
                );

                System.out.println(
                        "sess = "
                                + sess
                );
            }


            if (speditorLogin == null) {

                System.out.println(
                        "WARNING: SpeditorLogin COOKIE NOT FOUND."
                );

            } else {

                System.out.println(
                        "SUCCESS: SpeditorLogin COOKIE FOUND."
                );
            }


            // =====================================================
            // 7. AUTHENTICATED REQUEST
            // =====================================================

            if (sess != null) {

                System.out.println();
                System.out.println("================================");
                System.out.println("7. AUTHENTICATED REQUEST");
                System.out.println("================================");

                HttpRequest authenticatedRequest =
                        HttpRequest.newBuilder()
                                .uri(URI.create(LOGIN_URL))
                                .header(
                                        "User-Agent",
                                        "Mozilla/5.0"
                                )
                                .GET()
                                .build();


                HttpResponse<byte[]> authenticatedResponse =
                        client.send(
                                authenticatedRequest,
                                HttpResponse.BodyHandlers.ofByteArray()
                        );


                System.out.println(
                        "AUTH STATUS: "
                                + authenticatedResponse.statusCode()
                );

                System.out.println(
                        "AUTH URL: "
                                + authenticatedResponse.uri()
                );

                String page =
                        new String(
                                authenticatedResponse.body(),
                                Charset.forName(
                                        "Windows-1251"
                                )
                        );

                System.out.println(
                        "RESPONSE SIZE: "
                                + page.length()
                );


                System.out.println();
                System.out.println(
                        "RESPONSE PREVIEW:"
                );

                System.out.println(
                        page.substring(
                                0,
                                Math.min(
                                        page.length(),
                                        1000
                                )
                        )
                );
            }


            System.out.println();
            System.out.println("================================");
            System.out.println("TEST FINISHED");
            System.out.println("================================");


        } catch (Exception e) {

            System.out.println();
            System.out.println("================================");
            System.out.println("ERROR");
            System.out.println("================================");

            e.printStackTrace();
        }
    }


    // =============================================================
    // PRINT COOKIES
    // =============================================================

    private static void printCookies(
            CookieManager cookieManager,
            String title
    ) {

        System.out.println();
        System.out.println("================================");
        System.out.println(title);
        System.out.println("================================");

        List<HttpCookie> cookies =
                cookieManager
                        .getCookieStore()
                        .getCookies();

        if (cookies.isEmpty()) {

            System.out.println(
                    "COOKIE STORE IS EMPTY."
            );

            return;
        }

        for (HttpCookie cookie : cookies) {

            System.out.println(
                    cookie.getName()
                            + "="
                            + cookie.getValue()
                            + " | domain="
                            + cookie.getDomain()
                            + " | path="
                            + cookie.getPath()
                            + " | secure="
                            + cookie.getSecure()
                            + " | httpOnly="
                            + cookie.isHttpOnly()
            );
        }
    }


    // =============================================================
    // FIND COOKIE
    // =============================================================

    private static String findCookie(
            CookieManager cookieManager,
            String name
    ) {

        for (
                HttpCookie cookie :
                cookieManager
                        .getCookieStore()
                        .getCookies()
        ) {

            if (
                    cookie.getName()
                            .equalsIgnoreCase(name)
            ) {

                return cookie.getValue();
            }
        }

        return null;
    }


    // =============================================================
    // URL ENCODE
    // =============================================================

    private static String urlEncode(
            String value
    ) {

        return java.net.URLEncoder
                .encode(
                        value,
                        Charset.forName(
                                "Windows-1251"
                        )
                );
    }
}

