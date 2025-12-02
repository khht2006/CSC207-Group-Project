package interface_adapter.geocode;

import entity.Location;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for the Geocode/Search Location use case.
 * Holds the state of the view: the list of found locations and any error messages.
 */
public class GeocodeViewModel {

    private List<Location> locations = new ArrayList<>();
    private List<String> suggestions = new ArrayList<>();
    private String errorMessage = null;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        List<Location> oldLocations = this.locations;
        this.locations = locations;
        support.firePropertyChange("locations", oldLocations, locations);
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        List<String> oldSuggestions = this.suggestions;
        this.suggestions = suggestions;
        support.firePropertyChange("suggestions", oldSuggestions, suggestions);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        String oldError = this.errorMessage;
        this.errorMessage = errorMessage;
        support.firePropertyChange("errorMessage", oldError, errorMessage);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
