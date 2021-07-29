package de.fraunhofer.iosb.svs.analysishub.views.addimplementationview;

import de.fraunhofer.iosb.svs.analysishub.data.service.*;
import de.fraunhofer.iosb.svs.analysishub.views.main.MainView;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.HashMap;
import java.util.Map;

@Route(value = "implementation", layout = MainView.class)
@PageTitle("Add Implementation")
@CssImport("./styles/views/addimplementation/add-implementation-view.css")
public class AddImplementationView extends Div {

    private Map<Tab, Component> tabsToPages = new HashMap<>();

    public AddImplementationView(PhaseService phaseService,
                                 TopicService topicService,
                                 ImplementationService implementationService,
                                 OntologyDependencyService ontologyDependencyService,
                                 SwrlRuleService swrlRuleService,
                                 JenaRuleService jenaRuleService) {
        addClassName("add-implementation-view");

        add(createTitle());
        Tab swrlRuleTab = new Tab("SWRL Rule");
        Tab jenaRuleTab = new Tab("Jena Rule");
        Tab processingModuleTab = new Tab("Processing Module");

        Div swrlRuleDiv = new AddSwrlRuleView(phaseService, implementationService, ontologyDependencyService, swrlRuleService);
        Div jenaRuleDiv = new AddJenaRuleView(phaseService, implementationService, ontologyDependencyService, jenaRuleService);
        jenaRuleDiv.setVisible(false);
        Div processingModuleDiv = new AddProcessingModuleView(phaseService, topicService, implementationService, ontologyDependencyService);
        processingModuleDiv.setVisible(false);

        tabsToPages.put(swrlRuleTab, swrlRuleDiv);
        tabsToPages.put(jenaRuleTab, jenaRuleDiv);
        tabsToPages.put(processingModuleTab, processingModuleDiv);

        Tabs tabs = new Tabs(swrlRuleTab, jenaRuleTab, processingModuleTab);
        Div divs = new Div(swrlRuleDiv, jenaRuleDiv, processingModuleDiv);

        // switches the three tabs as they are selected
        tabs.addSelectedChangeListener(event -> {
            tabsToPages.values().forEach(div -> div.setVisible(false));
            Component selectedDiv = tabsToPages.get(tabs.getSelectedTab());
            selectedDiv.setVisible(true);
        });
        add(tabs, divs);
    }

    private Component createTitle() {
        return new H3("Implementation");
    }
}
