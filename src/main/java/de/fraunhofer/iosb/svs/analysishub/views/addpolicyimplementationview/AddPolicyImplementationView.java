package de.fraunhofer.iosb.svs.analysishub.views.addpolicyimplementationview;

import de.fraunhofer.iosb.svs.analysishub.data.entity.Policy;
import de.fraunhofer.iosb.svs.analysishub.data.entity.PolicyImplementation;
import de.fraunhofer.iosb.svs.analysishub.data.entity.Query;
import de.fraunhofer.iosb.svs.analysishub.data.entity.projections.ResourceUriProjection;
import de.fraunhofer.iosb.svs.analysishub.data.service.PolicyImplementationService;
import de.fraunhofer.iosb.svs.analysishub.data.service.PolicyService;
import de.fraunhofer.iosb.svs.analysishub.data.service.QueryService;
import de.fraunhofer.iosb.svs.analysishub.exceptions.ResourceAlreadyExistsException;
import de.fraunhofer.iosb.svs.analysishub.views.ResourceDataProvider;
import de.fraunhofer.iosb.svs.analysishub.views.main.MainView;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "policyimplementation", layout = MainView.class)
@PageTitle("Add Policy Implementation")
@CssImport("./styles/views/addpolicyimplementation/add-policy-implementation-view.css")
public class AddPolicyImplementationView extends Div {
    private TextField name = new TextField("Name");
    private TextArea description = new TextArea("Description");
    private ComboBox<ResourceUriProjection> query = new ComboBox<>("Query");
    private ComboBox<ResourceUriProjection> policy = new ComboBox<>("Policy");

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<PolicyImplementation> binder = new Binder<>(PolicyImplementation.class);

    public AddPolicyImplementationView(PolicyImplementationService policyImplementationService, QueryService queryService, PolicyService policyService) {
        addClassName("add-policy-implementation-view");

        add(createTitle());
        add(createFormLayout(queryService, policyService));
        add(createButtonLayout());

        binder.forField(name).asRequired("Name needed").withValidator(new RegexpValidator("No whitespaces allowed", "^[a-zA-Z0-9\\-_]*")).bind(PolicyImplementation::getLocalName, PolicyImplementation::setUriByLocalName);
        binder.bind(description, PolicyImplementation::getDescription, PolicyImplementation::setDescription);
        binder.forField(query).asRequired("Query needed").bind(policyImplementation -> {
                    Query queryBind = policyImplementation.getQuery();
                    if (queryBind == null) {
                        return null;
                    }
                    return new ResourceUriProjection(queryBind.getId(), queryBind.getUri());
                },
                ((policyImplementation, resourceUriProjection) -> policyImplementation.setQuery(queryService.getOrThrow(resourceUriProjection.getId()))));
        binder.forField(policy).asRequired("Policy needed").bind(policyImplementation -> {
                    Policy policyBind = policyImplementation.getPolicy();
                    if (policyBind == null) {
                        return null;
                    }
                    return new ResourceUriProjection(policyBind.getId(), policyBind.getUri());
                },
                (policyImplementation, resourceUriProjection) -> policyImplementation.setPolicy(policyService.getOrThrow(resourceUriProjection.getId())));

        clearForm();

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            if (binder.validate().isOk()) {
                try {
                    policyImplementationService.update(binder.getBean());
                } catch (ResourceAlreadyExistsException raee) {
                    String error = "Policy '" + binder.getBean().getLocalName() + "' already exists.";
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

    private void clearForm() {
        binder.setBean(new PolicyImplementation());
    }

    private Component createTitle() {
        return new H3("Policy Implementation");
    }

    private Component createFormLayout(QueryService queryService, PolicyService policyService) {
        FormLayout formLayout = new FormLayout();
        description.setPlaceholder("Description here ...");

        query.setDataProvider(new ResourceDataProvider<>(queryService));
        query.setClearButtonVisible(true);
        query.setItemLabelGenerator(ResourceUriProjection::getLocalName);

        policy.setDataProvider(new ResourceDataProvider<>(policyService));
        policy.setClearButtonVisible(true);
        policy.setItemLabelGenerator(ResourceUriProjection::getLocalName);

        formLayout.add(name, description, query, policy);
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
}
