import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;

public final class BackAction {

    /**
     * metoda ce rezolva actiunea de "back" intre pagini
     * @param currinfo
     * @param pagesList
     * @param action
     * @param inputdata
     * @param output
     * @param objectMapper
     */
    public static void backMethod(final CurrInfo currinfo, final ArrayList<CurrInfo> pagesList,
                                  final Actions action, final InputData inputdata,
                                  final ArrayNode output, final ObjectMapper objectMapper) {

        if (currinfo.getName().equals("Homepage autentificat") || pagesList == null) {
            GenerateOutput.printError(output, objectMapper);
        } else {
            pagesList.remove(pagesList.size() - 1);

            // currinfo va retine informatiile penultimei pagini (care devine ultima dupa stergere)
            CurrInfo previousPage = pagesList.get(pagesList.size() - 1);

            // actualizez numele paginii
            currinfo.setName(previousPage.getName());

            // campurile unui user trebuie pastrate daca fac back si ma intorc cu o pagina inapoi,
            // ex: cumpar/ vad un film si fac back => filmul trebuie sa ramana in lista aferenta
            // facand cu set pastrez mereu modificarile curente ale user ului
            currinfo.setUser(previousPage.getUser());

            // actualizez lista curenta de filme
            currinfo.setMoviesList(previousPage.getMoviesList());

            // actualizez si filmul pt see details
            currinfo.setMovie(previousPage.getMovie());

            if (currinfo.getName().equals("movies")) {
                GenerateOutput.printFullOutput(currinfo, output, objectMapper);
            }
        }

    }
}
