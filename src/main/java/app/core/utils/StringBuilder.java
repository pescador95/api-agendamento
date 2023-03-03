package app.core.utils;

import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class StringBuilder {

    public static String makeMaskCnpjFormatter(String pCnpj) throws ParseException {

        MaskFormatter mask;
        mask = new MaskFormatter("###.###.###/####-##");
        mask.setValueContainsLiteralCharacters(false);
        return mask.valueToString(pCnpj);

    }

    public String makeMaskCpfFormatter(String pCpf) throws ParseException {

        MaskFormatter mask;
        mask = new MaskFormatter("###.###.###-##");
        mask.setValueContainsLiteralCharacters(false);
        return mask.valueToString(pCpf);
    }

    public  String makeMaskRgFormatter(String pRg) throws ParseException {

        MaskFormatter mask;
        mask = new MaskFormatter("##.###.###-#");
        mask.setValueContainsLiteralCharacters(false);
        return mask.valueToString(pRg);
    }

    public static String removeChars(String pText) throws ParseException {

        String text = pText.replaceAll("^\\[", "");
        text.replaceAll("\\]", "");
        return text;
    }

    public static List<String> removeCharsList(List<String> pText) throws ParseException {
        List<String> returnTexts = new ArrayList<>();

         pText.forEach(text -> {
            String textReturn;
            textReturn = text.replaceAll("^\\[", "");
            textReturn = text.replaceAll("\\]", "");
            returnTexts.add(textReturn);
        });
    return returnTexts;
    }

    public static String makeOnlyNumbers(String text) throws ParseException {
        return text.replaceAll("[^0-9]+", "");
    }

}