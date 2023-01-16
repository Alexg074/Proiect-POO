import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;

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

            // lista de currinfo, ce va contine paginile vizitate
            ArrayList<CurrInfo> pagesList = new ArrayList<>();

            // retin pagina curenta la fiecare pas
            CurrInfo currPage = new CurrInfo();
            currPage.setName("Homepage neautentificat");

            for (Actions action : inputdata.getActions()) {
                // verific de ce tip este actiunea curenta primita
                if (action.getType().equals("on page")) {
                    OnPageActions onPageActions = OnPageActions.getInstance();
                    onPageActions.onPageMethod(currPage, pagesList, action,
                                                inputdata, output, objectMapper);

                } else if (action.getType().equals("change page")) {
                    ChangePageActions changePageActions = ChangePageActions.getInstance();
                    changePageActions.changePageMethod(currPage, pagesList, action,
                                                        inputdata, output, objectMapper);

                } else if (action.getType().equals("database")) {
                    DatabaseActions databaseActions = DatabaseActions.getInstance();
                    if (action.getFeature().equals("add")) {
                        databaseActions.addMethod(currPage, action, inputdata,
                                                    output, objectMapper);
                    } else if (action.getFeature().equals("delete")) {
                        databaseActions.deleteMethod(currPage, action, inputdata,
                                                        output, objectMapper);
                    }

                } else if (action.getType().equals("back")) {
                    BackAction backAction = BackAction.getInstance();
                    backAction.backMethod(currPage, pagesList, action, inputdata,
                                            output, objectMapper);
                }
            }

            if (currPage.getUser() != null) {
                if (currPage.getUser().getNotifications() == null) {
                    currPage.getUser().setNotifications(new ArrayList<Notification>());
                }

                if (currPage.getUser().getCredentials().getAccountType().equals("premium")) {
                    currPage.setName("Premium User");
                    currPage.getUser().getNotifications().add(new Notification(
                                    "No recommendation", "Recommendation"));
                    GenerateOutput.printFullOutput(currPage, output, objectMapper);
                }
            }

            // arg[1] = output
            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            objectWriter.writeValue(new File(args[1]), output);
        }
    }
}

