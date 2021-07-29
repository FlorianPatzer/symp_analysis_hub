package de.fraunhofer.iosb.svs.analysishub.views.addqueryview;

import de.fraunhofer.iosb.svs.analysishub.data.entity.AnalyticQuery;
import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.ResourceUriProjection;
import de.fraunhofer.iosb.svs.analysishub.data.service.ImplementationService;
import de.fraunhofer.iosb.svs.analysishub.data.service.OntologyDependencyService;
import de.fraunhofer.iosb.svs.analysishub.data.service.QueryService;
import de.fraunhofer.iosb.svs.analysishub.exceptions.ResourceAlreadyExistsException;
import de.fraunhofer.iosb.svs.analysishub.views.ResourceDataProvider;
import de.fraunhofer.iosb.svs.analysishub.views.main.MainView;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.stream.Collectors;

@Route(value = "query", layout = MainView.class)
@PageTitle("Add Query")
@CssImport("./styles/views/addquery/add-query-view.css")
public class AddQueryView extends Div {

    private TextField name = new TextField("Name");
    private TextArea query = new TextArea("Query");
    private MultiselectComboBox<ResourceUriProjection> dependsOnImplementation = new MultiselectComboBox<>("Depends on Implementation");
    private MultiselectComboBox<ResourceUriProjection> dependsOnOntologyDependency = new MultiselectComboBox<>("Depends on Ontology Dependency");

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<AnalyticQuery> binder = new Binder<>(AnalyticQuery.class);

    public AddQueryView(QueryService queryService, ImplementationService implementationService, OntologyDependencyService ontologyDependencyService) {
        addClassName("add-query-view");

        add(createTitle());
        add(createFormLayout(implementationService, ontologyDependencyService));
        add(createButtonLayout());

        binder.forField(name).asRequired("Name needed").withValidator(new RegexpValidator("No whitespaces allowed", "^[a-zA-Z0-9\\-_]*")).bind(AnalyticQuery::getLocalName, AnalyticQuery::setUriByLocalName);
        binder.forField(query).asRequired("Query needed").bind(AnalyticQuery::getQuery, AnalyticQuery::setQuery);
        binder.bind(dependsOnImplementation,
                query -> query.getImplementations().stream().map(implementation -> new ResourceUriProjection(implementation.getId(), implementation.getUri())).collect(Collectors.toSet()),
                (query, resourceUriProjections) -> query.setImplementations(resourceUriProjections.stream()
                        .map(resourceUriProjection -> implementationService.getOrThrow(resourceUriProjection.getId()))
                        .collect(Collectors.toList()))
        );
        binder.bind(dependsOnOntologyDependency,
                query -> query.getOntologyDependencies().stream().map(ontologyDependency -> new ResourceUriProjection(ontologyDependency.getId(), ontologyDependency.getUri())).collect(Collectors.toSet()),
                (query, resourceUriProjections) -> query.setOntologyDependencies(resourceUriProjections.stream()
                        .map(resourceUriProjection -> ontologyDependencyService.getOrThrow(resourceUriProjection.getId()))
                        .collect(Collectors.toList()))
        );
        clearForm();

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            if (binder.validate().isOk()) {
                try {
                    queryService.update(binder.getBean());
                } catch (ResourceAlreadyExistsException raee) {
                    String error = binder.getBean().getClass().getSimpleName() + " '" + binder.getBean().getLocalName() + "' already exists.";
                    Notification.show(error);
                    name.setErrorMessage(error);
                    name.setInvalid(true);
                    return;
                }
                name.setInvalid(false);
                name.setErrorMessage(null);
                Notification.show(binder.getBean().getClass().getSimpleName() + " stored.");
                clearForm();
            }
        });
    }

    private Component createTitle() {
        return new H3("Query");
    }

    private Component createFormLayout(ImplementationService implementationService, OntologyDependencyService ontologyDependencyService) {
        FormLayout formLayout = new FormLayout();

        dependsOnImplementation.setDataProvider(new ResourceDataProvider<>(implementationService));
        dependsOnImplementation.setClearButtonVisible(true);
        dependsOnImplementation.setItemLabelGenerator(ResourceUriProjection::getLocalName);

        dependsOnOntologyDependency.setDataProvider(new ResourceDataProvider<>(ontologyDependencyService));
        dependsOnOntologyDependency.setClearButtonVisible(true);
        dependsOnOntologyDependency.setItemLabelGenerator(ResourceUriProjection::getLocalName);

        formLayout.add(name, dependsOnImplementation, dependsOnOntologyDependency, query);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }

    private void clearForm() {
        this.binder.setBean(new AnalyticQuery());
    }
}
