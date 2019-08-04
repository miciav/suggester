package it.disco.unimib.suggester.controller;


import it.disco.unimib.suggester.model.table.Column;
import it.disco.unimib.suggester.model.table.TableSchema;
import it.disco.unimib.suggester.service.Orchestrator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/suggester/api")
public class SuggestController {

    @Getter
    @Setter
    private boolean test = false;

    private final Orchestrator orchestrator;


    public SuggestController(Orchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PutMapping(value = "/schema/translate", consumes = "application/json", produces = "application/json")
    public TableSchema putTranslateSchema(@Valid @RequestBody TableSchema schema,
                                          @RequestParam(name = "preferredSummaries[]", required = false) String[] preferredSummaries,
                                          @RequestParam(name = "suggester", required = false) TypeSuggester suggester) {
        if (test) {
            String summaries = String.join(",", asList(preferredSummaries));
            System.out.println(summaries);
        }

        String suggesterName = Objects.nonNull(suggester) ? suggester.getValue() : null;

        return preferredSummaries != null
                ? orchestrator.translateAndSuggest(schema, asList(preferredSummaries), suggesterName)
                : orchestrator.translateAndSuggest(schema, emptyList(), suggesterName);
    }


    @PutMapping(value = "column/translate", consumes = "application/json", produces = "application/json")
    public Column putTranslateColumn(@Valid @RequestBody Column column,
                                     @RequestParam(name = "preferredSummaries[]", required = false) String[] preferredSummaries,
                                     @RequestParam(name = "suggester", required = false) TypeSuggester suggester) {
        if (test) System.out.println(column.toString());

        String suggesterName = Objects.nonNull(suggester) ? suggester.getValue() : null;

        return preferredSummaries != null
                ? orchestrator.translateAndSuggest(column, asList(preferredSummaries), suggesterName)
                : orchestrator.translateAndSuggest(column, emptyList(), suggesterName);
    }


    @GetMapping(value = "/summaries", produces = "application/json")
    public List<String> getSummaries(@RequestParam(name = "suggester", required = false) TypeSuggester suggester) {

        return orchestrator.getAvailableSummaries(suggester.getValue());
    }


    enum TypeSuggester {
        ABSTAT("abstat"), LOV("lov");

        private final String value;

        TypeSuggester(String suggester) {
            this.value = suggester;

        }

        String getValue() {
            return value;
        }
    }



}
