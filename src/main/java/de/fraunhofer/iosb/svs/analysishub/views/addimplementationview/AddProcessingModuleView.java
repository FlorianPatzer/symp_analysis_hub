package de.fraunhofer.iosb.svs.analysishub.views.addimplementationview;

import de.fraunhofer.iosb.svs.analysishub.data.entity.ProcessingModule;
import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.ResourceUriProjection;
import de.fraunhofer.iosb.svs.analysishub.data.service.ImplementationService;
import de.fraunhofer.iosb.svs.analysishub.data.service.OntologyDependencyService;
import de.fraunhofer.iosb.svs.analysishub.data.service.PhaseService;
import de.fraunhofer.iosb.svs.analysishub.data.service.TopicService;
import de.fraunhofer.iosb.svs.analysishub.exceptions.ResourceAlreadyExistsException;
import de.fraunhofer.iosb.svs.analysishub.views.ResourceDataProvider;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.validator.RegexpValidator;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.stream.Collectors;

public class AddProcessingModuleView extends Div {
    private final DataProvider<ResourceUriProjection, String> ontologyDependencyDataProvider;
    private final DataProvider<ResourceUriProjection, String> implementationDataProvider;
    private final Binder<ProcessingModule> binder = new Binder<>(ProcessingModule.class);
    private TextField name = new TextField("Name");
    private TextArea description = new TextArea("Description");
    private ComboBox<PhaseService.StaticPhase> phase = new ComboBox<>("Phase");
    private MultiselectComboBox<ResourceUriProjection> dependsOnImplementation = new MultiselectComboBox<>("Depends On Implementation");
    private MultiselectComboBox<ResourceUriProjection> dependsOnOntologyDependency = new MultiselectComboBox<>("Depends On Ontology Dependency");
    private TextField topic = new TextField("Topic");
    private TextField imageName = new TextField("Image name");
    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    public AddProcessingModuleView(PhaseService phaseService,
                                   TopicService topicService,
                                   ImplementationService implementationService,
                                   OntologyDependencyService ontologyDependencyService) {

        ontologyDependencyDataProvider = new ResourceDataProvider<>(ontologyDependencyService);
        implementationDataProvider = new ResourceDataProvider<>(implementationService);

        add(createFormLayout());
        add(createButtonLayout());

        binder.forField(name).asRequired("Name needed").withValidator(new RegexpValidator("No whitespaces allowed", "^[a-zA-Z0-9\\-_]*")).bind(ProcessingModule::getLocalName, ProcessingModule::setUriByLocalName);
        binder.bind(description, ProcessingModule::getDescription, ProcessingModule::setDescription);
        binder.forField(phase).asRequired("Phase needed").bind(processingModule -> phaseService.getStaticPhaseByPhase(processingModule.getPhase()),
                (processingModule, staticPhase) -> processingModule.setPhase(phaseService.getPhaseByStaticPhase(staticPhase)));
        binder.bind(dependsOnImplementation,
                processingModule -> processingModule.getDependsOnImplementation().stream()
                        .map(implementation -> new ResourceUriProjection(implementation.getId(), implementation.getUri()))
                        .collect(Collectors.toSet()),
                (processingModule, resourceUriProjections) -> processingModule.setDependsOnImplementation(
                        resourceUriProjections.stream()
                                .map(resourceUriProjection -> implementationService.getOrThrow(resourceUriProjection.getId()))
                                .collect(Collectors.toList())
                ));
        binder.bind(dependsOnOntologyDependency,
                processingModule -> processingModule.getDependsOnOntologyDependency().stream()
                        .map(ontologyDependency -> new ResourceUriProjection(ontologyDependency.getId(), ontologyDependency.getUri()))
                        .collect(Collectors.toSet()),
                (processingModule, resourceUriProjections) -> processingModule.setDependsOnOntologyDependency(
                        resourceUriProjections.stream()
                                .map(resourceUriProjection -> ontologyDependencyService.getOrThrow(resourceUriProjection.getId()))
                                .collect(Collectors.toList())
                ));
        binder.forField(topic).asRequired("Topic needed").bind(processingModule -> {
                    if (processingModule.getTopic() != null) {
                        return processingModule.getTopic().getLocalName();
                    } else {
                        return null;
                    }
                },
                (processingModule, topicString) -> processingModule.setTopic(topicService.getTopicByNameOrElseCreate(topicString)));
        binder.forField(imageName).asRequired("Image name needed").bind(ProcessingModule::getImageName, ProcessingModule::setImageName);

        clearForm();

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            if (binder.validate().isOk()) {
                try {
                    implementationService.update(binder.getBean());
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

                implementationDataProvider.refreshAll();
                clearForm();
            }
        });
    }

    private void clearForm() {
        binder.setBean(new ProcessingModule());
    }

    private FormLayout createFormLayout() {
        description.setPlaceholder("Description here ...");
        phase.setItems(PhaseService.StaticPhase.values());
        phase.setItemLabelGenerator(PhaseService.StaticPhase::getLocalName);

        dependsOnImplementation.setDataProvider(implementationDataProvider);
        dependsOnImplementation.setClearButtonVisible(true);
        dependsOnImplementation.setItemLabelGenerator(ResourceUriProjection::getLocalName);

        dependsOnOntologyDependency.setDataProvider(ontologyDependencyDataProvider);
        dependsOnOntologyDependency.setClearButtonVisible(true);
        dependsOnOntologyDependency.setItemLabelGenerator(ResourceUriProjection::getLocalName);

        FormLayout formLayout = new FormLayout();
        formLayout.add(name, description, phase, dependsOnImplementation, dependsOnOntologyDependency, topic, imageName);
        return formLayout;
    }

    private HorizontalLayout createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }
}
