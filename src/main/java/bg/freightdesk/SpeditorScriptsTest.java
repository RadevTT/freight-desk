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

public class SpeditorScriptsTest {

    private static final Charset SPEDITOR_CHARSET =
            Charset.forName("windows-1251");

    public static void main(String[] args) throws Exception {

        System.out.println("================================");
        System.out.println("SPEDITOR JAVASCRIPT TEST");
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


        System.out.println(
                "POST URL:"
        );

        System.out.println(loginUrl);

        System.out.println();

        System.out.println("Sending:");
        System.out.println("w1=1");
        System.out.println("slog = NOT SENT");

        System.out.println(
                "username length = "
                        + username.length()
        );

        System.out.println(
                "password length = "
                        + password.length()
        );


        // ============================================================
        // 7. LOGIN REQUEST
        // ============================================================

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


        // ============================================================
        // 8. SEND LOGIN
        // ============================================================

        HttpResponse<String> loginResponse =
                client.send(
                        loginRequest,
                        HttpResponse.BodyHandlers.ofString(
                                SPEDITOR_CHARSET
                        )
                );


        System.out.println();

        System.out.println(
                "LOGIN STATUS: "
                        + loginResponse.statusCode()
        );


        // ============================================================
        // 9. SHOW COOKIES
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("3. COOKIES AFTER LOGIN");
        System.out.println("================================");

        cookieManager
                .getCookieStore()
                .getCookies()
                .forEach(cookie ->
                        System.out.println(
                                cookie.getName()
                                        + "="
                                        + cookie.getValue()
                        )
                );


        // ============================================================
        // 10. CHECK SESSION
        // ============================================================

        boolean hasSession =
                cookieManager
                        .getCookieStore()
                        .getCookies()
                        .stream()
                        .anyMatch(cookie ->
                                cookie.getName()
                                        .equalsIgnoreCase("sess")
                        );


        System.out.println();
        System.out.println("================================");
        System.out.println("4. SESSION CHECK");
        System.out.println("================================");


        if (!hasSession) {

            System.out.println();
            System.out.println(
                    "ERROR: sess COOKIE NOT FOUND."
            );

            System.out.println(
                    "LOGIN FAILED."
            );

            System.out.println(
                    "STOPPING."
            );

            return;
        }


        System.out.println();
        System.out.println(
                "SUCCESS: sess COOKIE FOUND."
        );

        System.out.println(
                "LOGIN SESSION WAS CREATED."
        );


        // ============================================================
        // 11. OPEN CARGO FORM
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("5. OPEN CARGO FORM");
        System.out.println("================================");


        String formUrl =
                "https://www1.speditor.net/cgi-bin/borsa.pl" +
                        "?act=tovar" +
                        "&tID=1788244045014590";


        System.out.println();
        System.out.println("GET URL:");
        System.out.println(formUrl);


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


        // ============================================================
        // 12. FORM RESPONSE
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("6. FORM RESPONSE");
        System.out.println("================================");

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


        // ============================================================
        // 13. FIND SCRIPT TAGS
        // ============================================================

        String html =
                formResponse.body();


        System.out.println();
        System.out.println("================================");
        System.out.println("7. JAVASCRIPT FILES");
        System.out.println("================================");


        Pattern scriptPattern =
                Pattern.compile(
                        "<script\\b[^>]*\\bsrc\\s*=\\s*[\"']?([^\\s\"'>]+)[\"']?[^>]*>",
                        Pattern.CASE_INSENSITIVE
                );


        Matcher scriptMatcher =
                scriptPattern.matcher(html);


        int scriptCount = 0;


        while (scriptMatcher.find()) {

            scriptCount++;

            String src =
                    scriptMatcher.group(1);


            System.out.println();
            System.out.println("--------------------------------");
            System.out.println(
                    "SCRIPT #" + scriptCount
            );
            System.out.println("--------------------------------");

            System.out.println(
                    "SRC:"
            );

            System.out.println(src);


            // ========================================================
            // CONVERT RELATIVE URL TO ABSOLUTE URL
            // ========================================================

            try {

                URI scriptUri =
                        URI.create(src);

                URI absoluteUri =
                        URI.create(formUrl)
                                .resolve(scriptUri);

                System.out.println();

                System.out.println(
                        "ABSOLUTE URL:"
                );

                System.out.println(
                        absoluteUri
                );

            } catch (Exception e) {

                System.out.println();

                System.out.println(
                        "Could not resolve URL."
                );
            }
        }


        System.out.println();

        System.out.println(
                "TOTAL EXTERNAL SCRIPTS: "
                        + scriptCount
        );


        // ============================================================
        // 14. SEARCH IMPORTANT JAVASCRIPT REFERENCES
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("8. JAVASCRIPT REFERENCES");
        System.out.println("================================");


        String[] searches = {

                "fromcountry",
                "tocountry",
                "tiptovar",
                "speshnost",
                "gLH",
                "postform",
                "option"

        };


        for (String search : searches) {

            int index =
                    html.toLowerCase()
                            .indexOf(
                                    search.toLowerCase()
                            );


            System.out.println();
            System.out.println("--------------------------------");
            System.out.println(
                    "SEARCH: " + search
            );
            System.out.println("--------------------------------");


            if (index == -1) {

                System.out.println(
                        "NOT FOUND"
                );

            } else {

                System.out.println(
                        "FOUND AT POSITION: "
                                + index
                );


                int start =
                        Math.max(
                                0,
                                index - 300
                        );


                int end =
                        Math.min(
                                html.length(),
                                index + 700
                        );


                System.out.println();

                System.out.println(
                        html.substring(
                                start,
                                end
                        )
                );
            }
        }


        // ============================================================
        // 15. SAFETY CHECK
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("9. SAFETY CHECK");
        System.out.println("================================");

        System.out.println(
                "LOGIN: YES"
        );

        System.out.println(
                "SESSION: YES"
        );

        System.out.println(
                "CARGO FORM: GET ONLY"
        );

        System.out.println(
                "JAVASCRIPT FILES: READ ONLY"
        );

        System.out.println(
                "CARGO POST: NO"
        );

        System.out.println(
                "ADVERTISEMENT PUBLISHED: NO"
        );


        // ============================================================
        // 16. FINISHED
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("JAVASCRIPT TEST FINISHED");
        System.out.println("================================");
    }


    // ================================================================
    // URL ENCODING
    // ================================================================

    private static String encode(String value) {

        return URLEncoder.encode(
                value,
                SPEDITOR_CHARSET
        );
    }
}