package de.fraunhofer.iosb.svs.analysishub.views.addpolicyanalysis;

import de.fraunhofer.iosb.svs.analysishub.data.entity.Policy;
import de.fraunhofer.iosb.svs.analysishub.data.service.PolicyService;
import de.fraunhofer.iosb.svs.analysishub.exceptions.ResourceAlreadyExistsException;
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
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Route(value = "policy", layout = MainView.class)
@PageTitle("Add Policy")
@CssImport("./styles/views/addpolicy/add-policy-view.css")
public class AddPolicyView extends Div {
    private static final Logger log = LoggerFactory.getLogger(AddPolicyView.class);

    private TextField name = new TextField("Name");
    private TextArea description = new TextArea("Description");

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private Binder<Policy> binder = new Binder<>(Policy.class);

    public AddPolicyView(PolicyService policyService) {
        addClassName("add-policy-view");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        binder.forField(name).asRequired("Name needed").withValidator(new RegexpValidator("No whitespaces allowed", "^[a-zA-Z0-9\\-_]*")).bind(Policy::getLocalName, Policy::setUriByLocalName);
        binder.bind(description, Policy::getDescription, Policy::setDescription);

        clearForm();

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            if (binder.validate().isOk()) {
                try {
                    policyService.update(binder.getBean());
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
        binder.setBean(new Policy());
    }

    private Component createTitle() {
        return new H3("Policy");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        description.setPlaceholder("Description here ...");
        formLayout.add(name, description);
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
