package bg.freightdesk.config;

import bg.freightdesk.entity.Country;
import bg.freightdesk.repository.CountryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CountryDataLoader implements CommandLineRunner {

    private final CountryRepository countryRepository;

    public CountryDataLoader(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public void run(String... args) {

        if (countryRepository.count() > 0) {
            return;
        }

        List<Country> countries = List.of(

                new Country("AL", "Албания"),
                new Country("AM", "Армения"),
                new Country("AT", "Австрия"),
                new Country("AZ", "Азербайджан"),
                new Country("BA", "Босна и Херцеговина"),
                new Country("BE", "Белгия"),
                new Country("BG", "България"),
                new Country("BY", "Беларус"),
                new Country("CH", "Швейцария"),
                new Country("CY", "Кипър"),
                new Country("CZ", "Чехия"),
                new Country("DE", "Германия"),
                new Country("DK", "Дания"),
                new Country("EE", "Естония"),
                new Country("ES", "Испания"),
                new Country("FI", "Финландия"),
                new Country("FR", "Франция"),
                new Country("GB", "Великобритания"),
                new Country("GE", "Грузия"),
                new Country("GR", "Гърция"),
                new Country("HR", "Хърватия"),
                new Country("HU", "Унгария"),
                new Country("IE", "Ирландия"),
                new Country("IQ", "Ирак"),
                new Country("IR", "Иран"),
                new Country("ISL", "Исландия"),
                new Country("IT", "Италия"),
                new Country("JO", "Йордания"),
                new Country("KG", "Киргизстан"),
                new Country("KZ", "Казахстан"),
                new Country("LI", "Лихтенщайн"),
                new Country("LT", "Литва"),
                new Country("LU", "Люксембург"),
                new Country("LV", "Латвия"),
                new Country("MC", "Монако"),
                new Country("MD", "Молдова"),
                new Country("MK", "Северна Македония"),
                new Country("MNE", "Черна гора"),
                new Country("MT", "Малта"),
                new Country("NL", "Нидерландия"),
                new Country("NO", "Норвегия"),
                new Country("PL", "Полша"),
                new Country("PO", "Португалия"),
                new Country("RKS", "Косово"),
                new Country("RO", "Румъния"),
                new Country("RU", "Русия"),
                new Country("SE", "Швеция"),
                new Country("SI", "Словения"),
                new Country("SK", "Словакия"),
                new Country("SR", "Сърбия"),
                new Country("SY", "Сирия"),
                new Country("TM", "Туркменистан"),
                new Country("TR", "Турция"),
                new Country("UA", "Украйна"),
                new Country("UZ", "Узбекистан")
        );

        countryRepository.saveAll(countries);

        System.out.println(
                "CountryDataLoader: loaded "
                        + countries.size()
                        + " countries."
        );
    }
}