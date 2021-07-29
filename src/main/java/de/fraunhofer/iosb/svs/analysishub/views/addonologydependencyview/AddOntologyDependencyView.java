package de.fraunhofer.iosb.svs.analysishub.views.addonologydependencyview;

import de.fraunhofer.iosb.svs.analysishub.data.entity.OntologyDependency;
import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.ResourceUriProjection;
import de.fraunhofer.iosb.svs.analysishub.data.service.OntologyDependencyService;
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
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.stream.Collectors;

@Route(value = "ontology-dependency", layout = MainView.class)
@PageTitle("Add Ontology Dependency")
@CssImport("./styles/views/addontologydependency/add-ontology-dependency-view.css")
public class AddOntologyDependencyView extends Div {
    private TextField name = new TextField("Name");
    private TextField prefix = new TextField("Prefix");
    private TextField ontologyUri = new TextField("Ontology Uri");

    private DataProvider<ResourceUriProjection, String> resourceDataProvider;
    private MultiselectComboBox<ResourceUriProjection> dependsOn = new MultiselectComboBox<>("Depends on Ontology Dependency");
    private MemoryBuffer memoryBuffer = new MemoryBuffer();
    private Upload ontologyDependencyFile = new Upload(memoryBuffer);


    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<OntologyDependency> binder = new Binder<>(OntologyDependency.class);

    public AddOntologyDependencyView(OntologyDependencyService ontologyDependencyService) {
        addClassName("add-ontology-dependency-view");

        resourceDataProvider = new ResourceDataProvider<>(ontologyDependencyService);

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        binder.forField(name).asRequired("Name needed").withValidator(new RegexpValidator("No whitespaces allowed", "^[a-zA-Z0-9\\-_]*")).bind(OntologyDependency::getLocalName, OntologyDependency::setUriByLocalName);
        binder.forField(prefix).asRequired("Prefix needed").bind(OntologyDependency::getPrefix, OntologyDependency::setPrefix);
        binder.bind(ontologyUri, OntologyDependency::getPrefix, OntologyDependency::setPrefix);
        binder.bind(dependsOn, ontologyDependency -> ontologyDependency.getDependsOnOntologies().stream()
                .map(ontologyDependency1 -> new ResourceUriProjection(ontologyDependency1.getId(), ontologyDependency1.getUri())).collect(Collectors.toSet()), (ontologyDependency, resourceUriProjections) -> {
            ontologyDependency.setDependsOnOntologies(resourceUriProjections.stream()
                    .map(resourceUriProjection -> ontologyDependencyService.getOrThrow(resourceUriProjection.getId()))
                    .collect(Collectors.toSet())
            );
        });

        clearForm();

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            if (binder.validate().isOk()) {
                try {
                    ontologyDependencyService.update(binder.getBean(), memoryBuffer.getInputStream());
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
        return new H3("Ontology Dependency");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();

        dependsOn.setDataProvider(resourceDataProvider);
        dependsOn.setClearButtonVisible(true);
        dependsOn.setItemLabelGenerator(ResourceUriProjection::getLocalName);

        ontologyDependencyFile.setMaxFiles(1);
        ontologyDependencyFile.setDropLabel(new Label("Upload ontology file"));
        ontologyDependencyFile.setAcceptedFileTypes("application/rdf+xml", "application/xml", "application/owl+xml", ".owl");

        ontologyDependencyFile.addSucceededListener(event -> {
            // set button true
            save.setEnabled(true);
        });
        ontologyDependencyFile.getElement().addEventListener("file-remove", event -> {
            save.setEnabled(false);
        });
        ontologyDependencyFile.addFileRejectedListener(event -> {
            // show error
            // set button false
            Notification.show("Error: " + event.getErrorMessage());
            save.setEnabled(false);
        });

        formLayout.add(name, prefix, ontologyUri, dependsOn, ontologyDependencyFile);
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
        save.setEnabled(false);
        resourceDataProvider.refreshAll();
        //ontologyDependencyFile.getElement().setPropertyJson("files", Json.createArray());
        this.binder.setBean(new OntologyDependency());
    }
}
