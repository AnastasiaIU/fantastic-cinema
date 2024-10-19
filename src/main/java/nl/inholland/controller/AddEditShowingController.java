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

public class AddEditShowingController implements Initializable {
    private final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Reference to the shared Database instance
    private final Database database;
    private final VBox root;
    private final Showing selectedShowing;
    private final boolean isAdd;

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

    public AddEditShowingController(Database database, VBox root, Showing selectedShowing) {
        this.database = database;
        this.root = root;
        this.selectedShowing = selectedShowing;
        isAdd = selectedShowing == null;
    }

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

    private void setDatePickerConverter() {
        StringConverter<LocalDate> converter = new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
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

    private Showing getShowing(LocalDate startDate, LocalTime duration, String title) {
        Showing showing;

        if (isAdd) {
            showing = new Showing(
                    -1,
                    LocalDateTime.of(startDate, LocalTime.of(startHoursSpinner.getValue(), startMinutesSpinner.getValue())),
                    0,
                    duration,
                    title,
                    new boolean[6][12]
            );
        } else {
            showing = selectedShowing;
            showing.setTitle(title);
            showing.setStartDateTime(
                    LocalDateTime.of(startDate, LocalTime.of(startHoursSpinner.getValue(), startMinutesSpinner.getValue()))
            );
            showing.setDuration(duration);
        }

        return showing;
    }

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

    private LocalDate tryParseLocalDate(String input) {
        try {
            return LocalDate.parse(input, dateFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void showTitleError(boolean show) {
        titlePromptLabel.setVisible(show);
        titleTextField.pseudoClassStateChanged(errorClass, show);
    }

    private void showStartDateError(boolean show) {
        startDateTimePromptLabel.setVisible(show);
        startDatePicker.pseudoClassStateChanged(errorClass, show);
    }

    private void showDurationTimeError(boolean show) {
        durationDateTimePromptLabel.setVisible(show);
        durationHoursSpinner.pseudoClassStateChanged(errorClass, show);
        durationMinutesSpinner.pseudoClassStateChanged(errorClass, show);
    }

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
            endDateTimeLabel.setText(dateFormatter.format(startDate) + " " + endTime);
        } else {
            endDateTimeLabel.setText("DD-MM-YYYY HH:MM");
        }
    }

    private String getDatePickerString(DatePicker datePicker) {
        LocalDate value = datePicker.getValue();

        if (value != null) {
            return dateFormatter.format(value);
        }

        String editorText = datePicker.getEditor().getText();
        return editorText.isEmpty() ? "" : editorText;
    }

    private void setSpinnerFactories() {
        setSpinnerFactory(startHoursSpinner, 23, isAdd ? null : selectedShowing.getStartDateTime().getHour());
        setSpinnerFactory(startMinutesSpinner, 59, isAdd ? null : selectedShowing.getStartDateTime().getMinute());
        setSpinnerFactory(durationHoursSpinner, 23, isAdd ? null : selectedShowing.getDuration().getHour());
        setSpinnerFactory(durationMinutesSpinner, 59, isAdd ? null : selectedShowing.getDuration().getMinute());
    }

    private void setSpinnerFactory(Spinner<Integer> spinner, int max, Integer initialValue) {
        spinner.setValueFactory(
                initialValue == null
                        ? new SpinnerValueFactory.IntegerSpinnerValueFactory(0, max)
                        : new SpinnerValueFactory.IntegerSpinnerValueFactory(0, max, initialValue)
        );
    }

    private void fillShowingData() {
        titleTextField.setText(selectedShowing.getTitle());
        startDatePicker.setValue(selectedShowing.getStartDateTime().toLocalDate());
        updateEndDateTimeLabel();
    }

    private void setListenersToRefreshAfterError() {
        titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            showTitleError(false);
        });

        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            showStartDateError(false);
            updateEndDateTimeLabel();
        });

        startHoursSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateEndDateTimeLabel();
        });

        startMinutesSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
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

    private void addListenersToButtons() {
        confirmButton.setOnAction(event -> {
            String title = titleTextField.getText();
            LocalDate startDate = tryParseLocalDate(getDatePickerString(startDatePicker));
            LocalTime duration = LocalTime.of(durationHoursSpinner.getValue(), durationMinutesSpinner.getValue());
            int durationInMinutes = duration.getHour() + duration.getMinute();

            displayErrors(title, startDate, durationInMinutes);

            if (!title.isEmpty() && startDate != null && durationInMinutes != 0) {
                Showing showing = getShowing(startDate, duration, title);

                database.addUpdateShowing(showing);
                openShowingsView();
            }
        });

        cancelButton.setOnAction(event -> {
            openShowingsView();
        });
    }
}
