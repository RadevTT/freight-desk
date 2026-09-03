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

public class SpeditorSelectSourceTest {

    private static final Charset SPEDITOR_CHARSET =
            Charset.forName("windows-1251");

    public static void main(String[] args) throws Exception {

        System.out.println("================================");
        System.out.println("SPEDITOR SELECT SOURCE TEST");
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
                        .uri(
                                URI.create(loginUrl)
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

        System.out.println(
                loginUrl
        );

        System.out.println();

        System.out.println(
                "Sending:"
        );

        System.out.println(
                "w1=1"
        );

        System.out.println(
                "slog = NOT SENT"
        );

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
                        .uri(
                                URI.create(loginUrl)
                        )
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
        System.out.println(
                "GET URL:"
        );

        System.out.println(
                formUrl
        );


        HttpRequest formRequest =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(formUrl)
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
        // 12. RESPONSE
        // ============================================================

        String html =
                formResponse.body();


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
                        + html.length()
                        + " characters"
        );


        // ============================================================
        // 13. FIND ALL SELECT TAGS
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("7. SELECT SOURCES");
        System.out.println("================================");


        Pattern selectPattern =
                Pattern.compile(
                        "<select\\b([^>]*)>(.*?)</select>",
                        Pattern.CASE_INSENSITIVE
                                | Pattern.DOTALL
                );


        Matcher selectMatcher =
                selectPattern.matcher(html);


        int selectCount = 0;


        while (selectMatcher.find()) {

            selectCount++;

            String attributes =
                    selectMatcher.group(1);

            String content =
                    selectMatcher.group(2);


            System.out.println();
            System.out.println("--------------------------------");
            System.out.println(
                    "SELECT #" + selectCount
            );
            System.out.println("--------------------------------");


            System.out.println(
                    "TAG:"
            );

            System.out.println(
                    "<select"
                            + attributes
                            + ">"
            );


            // --------------------------------------------------------
            // NAME
            // --------------------------------------------------------

            Matcher nameMatcher =
                    Pattern.compile(
                                    "\\bname\\s*=\\s*[\"']?([^\\s\"'>]+)",
                                    Pattern.CASE_INSENSITIVE
                            )
                            .matcher(attributes);


            if (nameMatcher.find()) {

                System.out.println(
                        "name = "
                                + nameMatcher.group(1)
                );

            } else {

                System.out.println(
                        "name = [none]"
                );
            }


            // --------------------------------------------------------
            // ID
            // --------------------------------------------------------

            Matcher idMatcher =
                    Pattern.compile(
                                    "\\bid\\s*=\\s*[\"']?([^\\s\"'>]+)",
                                    Pattern.CASE_INSENSITIVE
                            )
                            .matcher(attributes);


            if (idMatcher.find()) {

                System.out.println(
                        "id = "
                                + idMatcher.group(1)
                );

            } else {

                System.out.println(
                        "id = [none]"
                );
            }


            // --------------------------------------------------------
            // OPTIONS
            // --------------------------------------------------------

            Pattern optionPattern =
                    Pattern.compile(
                            "<option\\b([^>]*)>(.*?)</option>",
                            Pattern.CASE_INSENSITIVE
                                    | Pattern.DOTALL
                    );


            Matcher optionMatcher =
                    optionPattern.matcher(content);


            int optionCount = 0;


            while (optionMatcher.find()) {

                optionCount++;

                String optionAttributes =
                        optionMatcher.group(1);

                String optionText =
                        optionMatcher.group(2)
                                .replaceAll(
                                        "<[^>]+>",
                                        ""
                                )
                                .trim();


                Matcher valueMatcher =
                        Pattern.compile(
                                        "\\bvalue\\s*=\\s*[\"']?([^\\s\"'>]+)",
                                        Pattern.CASE_INSENSITIVE
                                )
                                .matcher(optionAttributes);


                String optionValue =
                        "[none]";


                if (valueMatcher.find()) {

                    optionValue =
                            valueMatcher.group(1);
                }


                System.out.println(
                        "OPTION #"
                                + optionCount
                                + ": value="
                                + optionValue
                                + " | text="
                                + optionText
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


        // ============================================================
        // 14. SEARCH FOR IMPORTANT JAVASCRIPT REFERENCES
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("8. JAVASCRIPT REFERENCES");
        System.out.println("================================");


        searchOccurrences(
                html,
                "fromcountry"
        );

        searchOccurrences(
                html,
                "tocountry"
        );

        searchOccurrences(
                html,
                "tiptovar"
        );

        searchOccurrences(
                html,
                "speshnost"
        );

        searchOccurrences(
                html,
                ".options"
        );

        searchOccurrences(
                html,
                ".add("
        );

        searchOccurrences(
                html,
                "innerHTML"
        );

        searchOccurrences(
                html,
                "option"
        );


        // ============================================================
        // 15. FIND JAVASCRIPT FILES
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("9. EXTERNAL JAVASCRIPT FILES");
        System.out.println("================================");


        Pattern scriptPattern =
                Pattern.compile(
                        "<script\\b[^>]*src\\s*=\\s*[\"']([^\"']+)[\"']",
                        Pattern.CASE_INSENSITIVE
                );


        Matcher scriptMatcher =
                scriptPattern.matcher(html);


        int scriptCount = 0;


        while (scriptMatcher.find()) {

            scriptCount++;

            System.out.println(
                    "SCRIPT #"
                            + scriptCount
                            + ": "
                            + scriptMatcher.group(1)
            );
        }


        if (scriptCount == 0) {

            System.out.println(
                    "NO EXTERNAL JAVASCRIPT FILES FOUND."
            );
        }


        // ============================================================
        // 16. INLINE JAVASCRIPT BLOCKS
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("10. INLINE JAVASCRIPT CHECK");
        System.out.println("================================");


        Pattern inlineScriptPattern =
                Pattern.compile(
                        "<script\\b(?![^>]*\\bsrc\\s*=)[^>]*>(.*?)</script>",
                        Pattern.CASE_INSENSITIVE
                                | Pattern.DOTALL
                );


        Matcher inlineScriptMatcher =
                inlineScriptPattern.matcher(html);


        int inlineCount = 0;


        while (inlineScriptMatcher.find()) {

            inlineCount++;

            String script =
                    inlineScriptMatcher.group(1);


            if (
                    script.contains("fromcountry")
                            || script.contains("tocountry")
                            || script.contains("tiptovar")
                            || script.contains("speshnost")
                            || script.contains("option")
            ) {

                System.out.println();
                System.out.println(
                        "INLINE SCRIPT #"
                                + inlineCount
                );

                System.out.println(
                        script.trim()
                );
            }
        }


        // ============================================================
        // 17. SAFETY CHECK
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("11. SAFETY CHECK");
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
                "SELECT OPTIONS: READ ONLY"
        );

        System.out.println(
                "JAVASCRIPT: READ ONLY"
        );

        System.out.println(
                "CARGO POST: NO"
        );

        System.out.println(
                "ADVERTISEMENT PUBLISHED: NO"
        );


        // ============================================================
        // 18. FINISHED
        // ============================================================

        System.out.println();
        System.out.println("================================");
        System.out.println("SELECT SOURCE TEST FINISHED");
        System.out.println("================================");
    }


    // ================================================================
    // SEARCH OCCURRENCES
    // ================================================================

    private static void searchOccurrences(
            String html,
            String search
    ) {

        Pattern pattern =
                Pattern.compile(
                        Pattern.quote(search),
                        Pattern.CASE_INSENSITIVE
                );


        Matcher matcher =
                pattern.matcher(html);


        int count = 0;


        while (matcher.find()) {

            count++;


            int start =
                    Math.max(
                            0,
                            matcher.start() - 150
                    );


            int end =
                    Math.min(
                            html.length(),
                            matcher.end() + 300
                    );


            String context =
                    html.substring(
                            start,
                            end
                    );


            System.out.println();
            System.out.println(
                    "SEARCH: "
                            + search
            );

            System.out.println(
                    "OCCURRENCE #"
                            + count
            );

            System.out.println(
                    context
                            .replace(
                                    "\n",
                                    " "
                            )
                            .replace(
                                    "\r",
                                    " "
                            )
            );
        }


        if (count == 0) {

            System.out.println();
            System.out.println(
                    "SEARCH: "
                            + search
                            + " -> NOT FOUND"
            );
        }
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