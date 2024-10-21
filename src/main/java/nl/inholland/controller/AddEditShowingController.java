package nl.inholland.controller;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import nl.inholland.Database;
import nl.inholland.model.Showing;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

/**
 * Controller for adding or editing showings in the application.
 * This class is responsible for managing the UI and logic when creating a new showing
 * or editing an existing one. It uses FXML components and binds their values
 * with the appropriate event handlers to manage user input and validate the showing data.
 * It implements the {@link Initializable} interface to set up initial values and listeners
 * for the UI components when the controller is initialized.
 */
public class AddEditShowingController implements Initializable {
    // Pseudo-class for error styling
    private final PseudoClass ERROR_CLASS = PseudoClass.getPseudoClass("error");
    // Formatter for date values in dd-MM-yyyy format
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Reference to the shared Database instance
    private final Database database;
    // Reference to the root VBox container of the scene
    private final VBox root;
    // The selected showing being edited, or null if adding a new showing
    private final Showing selectedShowing;
    // Boolean flag to check if the operation is to add a new showing
    private final boolean isAdd;

    // FXML-injected components
    @FXML
    private Label titleLabel;
    @FXML
    private Label titlePromptLabel;
    @FXML
    private Label startDateTimePromptLabel;
    @FXML
    private Label durationDateTimePromptLabel;
    @FXML
    private Label endDateTimeLabel;
    @FXML
    private Label roomAvailabilityPromptLabel;
    @FXML
    private TextField titleTextField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private Spinner<Integer> startHoursSpinner;
    @FXML
    private Spinner<Integer> startMinutesSpinner;
    @FXML
    private Spinner<Integer> durationHoursSpinner;
    @FXML
    private Spinner<Integer> durationMinutesSpinner;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;
    @FXML
    private CheckBox ageCheckBox;

    /**
     * Constructor for the controller.
     *
     * @param database        The database instance shared across controllers.
     * @param root            The root VBox container of the scene.
     * @param selectedShowing The showing to be edited; if null, a new showing is being added.
     */
    public AddEditShowingController(Database database, VBox root, Showing selectedShowing) {
        this.database = database;
        this.root = root;
        this.selectedShowing = selectedShowing;
        isAdd = selectedShowing == null;
    }

    /**
     * Initializes the controller and sets up initial values and listeners for the UI components.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titleLabel.setText(isAdd ? "Add Showing" : "Edit Showing");
        confirmButton.setText(isAdd ? "Add showing" : "Edit showing");

        setDatePickerConverter();
        setSpinnerFactories();

        if (!isAdd) {
            fillShowingData();
        }

        setListenersToRefreshAfterError();
        addListenersToButtons();
    }

    /**
     * Sets up the converter for the date picker to format and parse dates according to the set pattern.
     */
    private void setDatePickerConverter() {
        StringConverter<LocalDate> converter = new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return DATE_FORMATTER.format(date);
                }

                return "";
            }

            @Override
            public LocalDate fromString(String string) {
                return tryParseLocalDate(string);
            }
        };

        startDatePicker.setConverter(converter);
    }

    /**
     * Displays error messages for invalid inputs such as empty title, null start date, or zero duration.
     *
     * @param title     The title of the showing.
     * @param startDate The start date of the showing.
     * @param duration  The duration of the showing.
     */
    private void displayErrors(String title, LocalDate startDate, int duration) {
        if (title.isEmpty()) {
            showTitleError(true);
        }

        if (startDate == null) {
            showStartDateError(true);
        }

        if (duration == 0) {
            showDurationTimeError(true);
        }
    }

    /**
     * Creates or updates a showing based on user inputs.
     *
     * @param startDate The start date of the showing.
     * @param duration  The duration of the showing.
     * @param title     The title of the showing.
     * @return The updated or new showing instance.
     */
    private Showing getShowing(LocalDate startDate, LocalTime duration, String title, boolean isAgeChecked) {
        Showing showing;

        if (isAdd) {
            showing = new Showing(
                    -1,
                    LocalDateTime.of(startDate, LocalTime.of(startHoursSpinner.getValue(), startMinutesSpinner.getValue())),
                    0,
                    duration,
                    title,
                    new boolean[6][12],
                    isAgeChecked
            );
        } else {
            showing = selectedShowing;
            showing.setTitle(title);
            showing.setStartDateTime(
                    LocalDateTime.of(startDate, LocalTime.of(startHoursSpinner.getValue(), startMinutesSpinner.getValue()))
            );
            showing.setDuration(duration);
            showing.setIsAgeChecked(isAgeChecked);
        }

        return showing;
    }

    /**
     * Opens the showings view, replacing the current scene with the showings list view.
     */
    private void openShowingsView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/inholland/view/showings-view.fxml"));
            fxmlLoader.setController(new ShowingsController(database, root));
            Scene scene = new Scene(fxmlLoader.load());

            if (root.getChildren().size() > 1)
                root.getChildren().remove(1);
            root.getChildren().add(scene.getRoot());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses a date string into a LocalDate object using the specified date format.
     *
     * @param input The date string to parse.
     * @return The parsed LocalDate or null if parsing fails.
     */
    private LocalDate tryParseLocalDate(String input) {
        try {
            return LocalDate.parse(input, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Shows or hides the title error based on the input.
     *
     * @param show Boolean value to determine whether to show (true) or hide (false) the error indicator.
     */
    private void showTitleError(boolean show) {
        titlePromptLabel.setVisible(show);
        titleTextField.pseudoClassStateChanged(ERROR_CLASS, show);
    }

    /**
     * Shows or hides the start date error based on the input.
     *
     * @param show Boolean value to determine whether to show (true) or hide (false) the error indicator.
     */
    private void showStartDateError(boolean show) {
        startDateTimePromptLabel.setVisible(show);
        startDatePicker.pseudoClassStateChanged(ERROR_CLASS, show);
    }

    /**
     * Shows or hides the duration time error based on the input.
     *
     * @param show Boolean value to determine whether to show (true) or hide (false) the error indicator.
     */
    private void showDurationTimeError(boolean show) {
        durationDateTimePromptLabel.setVisible(show);
        durationHoursSpinner.pseudoClassStateChanged(ERROR_CLASS, show);
        durationMinutesSpinner.pseudoClassStateChanged(ERROR_CLASS, show);
    }

    /**
     * Displays or hides the room availability error based on the input.
     * This method sets the visibility of the room availability prompt label and updates the style
     * (pseudo-class state) for the DatePicker and Spinner components to indicate an error.
     *
     * @param show Boolean value to determine whether to show (true) or hide (false) the error indicator.
     */
    private void showRoomAvailabilityError(boolean show) {
        roomAvailabilityPromptLabel.setVisible(show);
        startDatePicker.pseudoClassStateChanged(ERROR_CLASS, show);
        startHoursSpinner.pseudoClassStateChanged(ERROR_CLASS, show);
        startMinutesSpinner.pseudoClassStateChanged(ERROR_CLASS, show);
    }

    /**
     * Updates the end date and time label based on the start date and duration selected by the user.
     * If the end time is on the next day, the start date is incremented by one day.
     */
    private void updateEndDateTimeLabel() {
        LocalDate startDate = tryParseLocalDate(getDatePickerString(startDatePicker));
        LocalTime duration = LocalTime.of(durationHoursSpinner.getValue(), durationMinutesSpinner.getValue());
        int durationInMinutes = duration.getHour() + duration.getMinute();

        if (startDate != null && durationInMinutes != 0) {
            // Get the start time from the spinners
            LocalTime startTime = LocalTime.of(startHoursSpinner.getValue(), startMinutesSpinner.getValue());

            // Calculate the end time by adding the duration
            LocalTime endTime = startTime.plusHours(duration.getHour()).plusMinutes(duration.getMinute());

            // Check if the end time is on the next day
            boolean isNextDay = endTime.isBefore(startTime);

            // If the end time is the next day, increment the start date by one day
            if (isNextDay) {
                startDate = startDate.plusDays(1);
            }

            // Update the label with the end date and time
            endDateTimeLabel.setText(DATE_FORMATTER.format(startDate) + " " + endTime);
        } else {
            endDateTimeLabel.setText("DD-MM-YYYY HH:MM");
        }
    }

    /**
     * Retrieves the date string from a DatePicker component.
     *
     * @param datePicker The DatePicker component from which to retrieve the date string.
     * @return The formatted date string or the raw editor text if no date is selected.
     */
    private String getDatePickerString(DatePicker datePicker) {
        LocalDate value = datePicker.getValue();

        if (value != null) {
            return DATE_FORMATTER.format(value);
        }

        String editorText = datePicker.getEditor().getText();
        return editorText.isEmpty() ? "" : editorText;
    }

    /**
     * Sets the value factories for the various time and duration spinners based on the state of the showing (add or edit).
     */
    private void setSpinnerFactories() {
        setSpinnerFactory(startHoursSpinner, 23, isAdd ? null : selectedShowing.getStartDateTime().getHour());
        setSpinnerFactory(startMinutesSpinner, 59, isAdd ? null : selectedShowing.getStartDateTime().getMinute());
        setSpinnerFactory(durationHoursSpinner, 23, isAdd ? null : selectedShowing.getDuration().getHour());
        setSpinnerFactory(durationMinutesSpinner, 59, isAdd ? null : selectedShowing.getDuration().getMinute());
    }

    /**
     * Sets up the value factory for a Spinner component, with an optional initial value.
     *
     * @param spinner      The Spinner component for which to set the value factory.
     * @param max          The maximum value allowed for the spinner.
     * @param initialValue The initial value to set, or null if no initial value is provided.
     */
    private void setSpinnerFactory(Spinner<Integer> spinner, int max, Integer initialValue) {
        spinner.setValueFactory(
                initialValue == null
                        ? new SpinnerValueFactory.IntegerSpinnerValueFactory(0, max)
                        : new SpinnerValueFactory.IntegerSpinnerValueFactory(0, max, initialValue)
        );
    }

    /**
     * Fills the UI components with data from the selected showing if editing an existing showing.
     */
    private void fillShowingData() {
        titleTextField.setText(selectedShowing.getTitle());
        startDatePicker.setValue(selectedShowing.getStartDateTime().toLocalDate());
        updateEndDateTimeLabel();
        ageCheckBox.setSelected(selectedShowing.getIsAgeChecked());
    }

    /**
     * Sets listeners on UI components to clear error indicators when the user changes input values.
     */
    private void setListenersToRefreshAfterError() {
        titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            showTitleError(false);
        });

        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            showStartDateError(false);
            showRoomAvailabilityError(false);
            updateEndDateTimeLabel();
        });

        startHoursSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            showRoomAvailabilityError(false);
            updateEndDateTimeLabel();
        });

        startMinutesSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            showRoomAvailabilityError(false);
            updateEndDateTimeLabel();
        });

        durationHoursSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            showDurationTimeError(false);
            updateEndDateTimeLabel();
        });

        durationMinutesSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            showDurationTimeError(false);
            updateEndDateTimeLabel();
        });
    }

    /**
     * Adds action listeners to the confirm and cancel buttons to handle user input and actions.
     */
    private void addListenersToButtons() {
        confirmButton.setOnAction(event -> {
            String title = titleTextField.getText().trim();
            LocalDate startDate = tryParseLocalDate(getDatePickerString(startDatePicker));
            LocalTime duration = LocalTime.of(durationHoursSpinner.getValue(), durationMinutesSpinner.getValue());
            int durationInMinutes = duration.getHour() + duration.getMinute();
            boolean isAgeChecked = ageCheckBox.isSelected();

            displayErrors(title, startDate, durationInMinutes);

            if (!title.isEmpty() && startDate != null && durationInMinutes != 0) {
                LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.of(startHoursSpinner.getValue(), startMinutesSpinner.getValue()));

                if (isRoomAvailable(startDateTime, duration)) {
                    Showing showing = getShowing(startDate, duration, title, isAgeChecked);
                    database.addUpdateShowing(showing);
                    openShowingsView();
                } else {
                    // Display an error message if the room is not available
                    showRoomAvailabilityError(true);
                }
            }
        });

        cancelButton.setOnAction(event -> {
            openShowingsView();
        });
    }

    /**
     * Checks if the room is available for a new showing by verifying the time slot does not overlap
     * with any existing showings in the database. If an existing showing is being edited, it skips the
     * check for that specific showing.
     *
     * @param startDateTime The start date and time of the new or edited showing.
     * @param duration      The duration of the new or edited showing.
     * @return True if the room is available for the given time slot; false if it overlaps with another showing.
     */
    private boolean isRoomAvailable(LocalDateTime startDateTime, LocalTime duration) {
        LocalDateTime endDateTime = startDateTime.plusHours(duration.getHour()).plusMinutes(duration.getMinute());

        for (Showing existingShowing : database.getShowings()) {
            // Skip the current showing if editing
            if (selectedShowing != null && existingShowing.getId() == selectedShowing.getId()) {
                continue;
            }

            LocalDateTime existingStart = existingShowing.getStartDateTime();
            LocalDateTime existingEnd = existingStart.plusHours(existingShowing.getDuration().getHour())
                    .plusMinutes(existingShowing.getDuration().getMinute());

            // Check if the new showing's time overlaps with any existing showing's time
            if (startDateTime.isBefore(existingEnd) && endDateTime.isAfter(existingStart)) {
                return false; // Room is not available
            }
        }
        return true; // Room is available
    }
}
