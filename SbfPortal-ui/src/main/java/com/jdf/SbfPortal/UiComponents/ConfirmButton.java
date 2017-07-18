package com.jdf.SbfPortal.UiComponents;

import com.vaadin.shared.Registration;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;


@SuppressWarnings("serial")
public class ConfirmButton extends Button {
	

	private Window  popup;
	private VerticalLayout content;
	private Button okButton;
	private Button cancelButton;
	private String okCaption="Yes";
	private String cancelCaption="No";
	private String confirmText="You really want to do this?";
	private String windowTitle="Are you sure?";
	  /**
	 * @param string
	 */
	public ConfirmButton(String caption) {
		super(caption);
	}

	/**
     *
     * @return the last Popup into which the Form was opened with
     * #openInModalPopup method or null if the form hasn't been use in window
     */
    public Window getPopup() {
        return popup;
    }

    /**
     * If the form is opened into a popup window using openInModalPopup(), you
     * you can use this method to close the popup.
     */
    public void closePopup() {
        if (popup != null) {
            popup.close();
            popup = null;
        }
    }
    public Window openInModalPopup() {
        popup = new Window(getModalWindowTitle(), getContent());
        popup.setModal(true);
        popup.setResizable(false);
        UI.getCurrent().addWindow(popup);
        return popup;
        
    }
    /**
     * @return A default toolbar containing save/cancel/delete buttons
     */
    private HorizontalLayout getToolbar() {
        return new HorizontalLayout(
                getOkButton(),
                getCancelButton()
        );
    }


    /**
	 * @return
	 */
	private Button getCancelButton() {
		if (cancelButton == null){
			cancelButton = new Button(getCancelCaption());
			cancelButton.addClickListener(e ->{closePopup();});
		}
		return cancelButton;
	}

	/**
	 * @return
	 */
	private Button getOkButton() {
		if (okButton == null){
			okButton = new Button(getOkCaption());
			okButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
		}
		return okButton;
	}

	/**
	 * @return
	 */
	private Component getContent() {
		if (content == null){
			content = new VerticalLayout();
			content.addComponent(new Label(getConfirmationText()));
			content.addComponent(getToolbar());
		}
		return content;
	}

	public String getModalWindowTitle() {
    	return (windowTitle);
    }
    public String getConfirmationText() {
    	return (confirmText);
    }

    public String getOkCaption() {
    	return (okCaption);
	}
    public String getCancelCaption() {
    	return (cancelCaption);
	}
    
    public void setOkCaption(String caption){
    	this.okCaption = caption;
    }
    
    public void setCancelCaption(String caption){
    	this.cancelCaption = caption;
    }
    
    public void setConfirmationText(String caption) {
    	this.confirmText = caption;
    }
    
    
    /* (non-Javadoc)
     * @see com.vaadin.ui.Button#addClickListener(com.vaadin.ui.Button.ClickListener)
     */
    @Override
    public Registration addClickListener(ClickListener listener) {
    	getOkButton().addClickListener(listener);
    	getOkButton().addClickListener(e -> {closePopup();});
    	return super.addClickListener(e -> {openInModalPopup();});
    }
}