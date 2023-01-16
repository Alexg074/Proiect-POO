import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(final String[] args) throws IOException {

        if (args.length >= 2) {
            System.out.println(args[0]);
            // arg[0] = input
            ObjectMapper objectMapper = new ObjectMapper();
            InputData inputdata = objectMapper.readValue(new File(args[0]), InputData.class);

            ArrayNode output = objectMapper.createArrayNode();

            // array de currinfo, ce va contine paginile vizitate
            ArrayList<CurrInfo> pagesList = new ArrayList<>();

            // retin pagina curenta la fiecare pas
            CurrInfo currPage = new CurrInfo();
            currPage.setName("Homepage neautentificat");

//            pagesList.add(currPage);

            for (Actions action : inputdata.getActions()) {
                // verific de ce tip este actiunea curenta primita
                if (action.getType().equals("on page")) {
                    OnPageActions onPageActions = OnPageActions.getInstance();
                    onPageActions.onPageMethod(currPage, pagesList, action, inputdata, output, objectMapper);

                } else if (action.getType().equals("change page")) {
                    ChangePageActions changePageActions = ChangePageActions.getInstance();
                    changePageActions.changePageMethod(currPage, pagesList, action, inputdata, output, objectMapper);

                } else if (action.getType().equals("database")) {
                    if (action.getFeature().equals("add")) {
                        DatabaseActions.addMethod(currPage, action, inputdata, output, objectMapper);
                    } else if (action.getFeature().equals("delete")) {
                        DatabaseActions.deleteMethod(currPage, action, inputdata, output, objectMapper);
                    }

                } else if (action.getType().equals("back")) {
                    BackAction.backMethod(currPage, pagesList, action, inputdata, output, objectMapper);

                    // GenerateOutput.printError(output, objectMapper);
                }
            }

            // arg[1] = output
            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            objectWriter.writeValue(new File(args[1]), output);
        }
    }
}

