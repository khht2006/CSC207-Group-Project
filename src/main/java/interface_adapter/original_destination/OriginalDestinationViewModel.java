package interface_adapter.original_destination;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class OriginalDestinationViewModel {
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    private String originText = "";
    private String destinationText = "";
    private String errorMessage;

    public String getOriginText() {
        return originText;
    }

    public void setOriginText(String originText) {
        String oldValue = this.originText;
        this.originText = originText;
        support.firePropertyChange("originText", oldValue, originText);
    }

    public String getDestinationText() {
        return destinationText;
    }

    public void setDestinationText(String destinationText) {
        String oldValue = this.destinationText;
        this.destinationText = destinationText;
        support.firePropertyChange("destinationText", oldValue, destinationText);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        String oldValue = this.errorMessage;
        this.errorMessage = errorMessage;
        support.firePropertyChange("errorMessage", oldValue, errorMessage);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
