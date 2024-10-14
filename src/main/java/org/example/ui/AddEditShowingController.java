package org.example.ui;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.model.Showing;
import org.example.model.User;
import org.example.service.ShowingService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddEditShowingController extends BaseController {
    private final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private ShowingService showingService;
    private Showing selectedShowing;
    private boolean isAdd;

    @FXML
    private Label titleLbl;
    @FXML
    private Label titlePromptLbl;
    @FXML
    private Label startDateTimePromptLbl;
    @FXML
    private Label durationPromptLbl;
    @FXML
    private Label endDateTimeLbl;
    @FXML
    private TextField titleTextField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private Spinner<Integer> hoursSpinner;
    @FXML
    private Spinner<Integer> minutesSpinner;
    @FXML
    private TextField durationTextField;
    @FXML
    private Button confirmButton;

    public void initialize(User currentUser, Menu clickedMenu, Showing selectedShowing, boolean isAdd) {
        super.initialize(currentUser, clickedMenu);

        showingService = new ShowingService();
        this.selectedShowing = selectedShowing;
        this.isAdd = isAdd;

        setAddEditView();
    }

    private void setAddEditView() {
        titleLbl.setText(isAdd ? "Add Showing" : "Edit Showing");
        confirmButton.setText(isAdd ? "Add showing" : "Edit showing");

        setDatePickerConverter();
        setSpinnerFactories();

        if (!isAdd) {
            fillShowingData();
        }

        setListeners();
    }

    private void setDatePickerConverter() {
        StringConverter converter = new StringConverter<LocalDate>() {
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

    @FXML
    protected void onConfirmButtonClick() {
        String title = titleTextField.getText();
        LocalTime duration = tryParseLocalTime(durationTextField.getText());
        LocalDate startDate = tryParseLocalDate(getDatePickerString(startDatePicker));

        displayErrors(title, startDate, duration);

        if (!title.isEmpty() && startDate != null && duration != null) {
            Showing showing = getShowing(startDate, duration, title);

            showingService.addUpdateShowing(showing);
            openShowingsView();
        }
    }

    private void displayErrors(String title, LocalDate startDate, LocalTime duration) {
        if (title.isEmpty()) {
            showTitleError(true);
        }

        if (startDate == null) {
            showStartDateError(true);
        }

        if (duration == null) {
            showDurationError(true);
        }
    }

    private Showing getShowing(LocalDate startDate, LocalTime duration, String title) {
        Showing showing;

        if (isAdd) {
            showing = new Showing(
                    -1,
                    LocalDateTime.of(startDate, LocalTime.of(hoursSpinner.getValue(), minutesSpinner.getValue())),
                    0,
                    duration,
                    title,
                    new boolean[6][12]
            );
        } else {
            showing = selectedShowing;
            showing.setTitle(title);
            showing.setStartDateTime(
                    LocalDateTime.of(startDate, LocalTime.of(hoursSpinner.getValue(), minutesSpinner.getValue()))
            );
            showing.setDuration(duration);
        }

        return showing;
    }

    @FXML
    protected void onCancelButtonClick() {
        openShowingsView();
    }

    private void openShowingsView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("showings-view.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) titleLbl.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/css/showings-view.css").toExternalForm());

            InitializableMenu controller = fxmlLoader.getController();
            controller.initialize(currentUser, showingsMenu);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LocalTime tryParseLocalTime(String input) {
        try {
            return LocalTime.parse(input);
        } catch (DateTimeParseException e) {
            return null;
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
        titlePromptLbl.setVisible(show);
        titleTextField.pseudoClassStateChanged(errorClass, show);
    }

    private void showDurationError(boolean show) {
        durationPromptLbl.setVisible(show);
        durationTextField.pseudoClassStateChanged(errorClass, show);
    }

    private void showStartDateError(boolean show) {
        startDateTimePromptLbl.setText("Wrong input. Please, follow the DD-MM-YYYY format.");
        startDateTimePromptLbl.setVisible(show);
        startDatePicker.pseudoClassStateChanged(errorClass, show);
    }

    private void updateEndDateTimeLabel() {
        LocalDate startDate = tryParseLocalDate(getDatePickerString(startDatePicker));
        LocalTime duration = tryParseLocalTime(durationTextField.getText());

        if (startDate != null && duration != null) {
            // Get the start time from the spinners
            LocalTime startTime = LocalTime.of(hoursSpinner.getValue(), minutesSpinner.getValue());

            // Calculate the end time by adding the duration
            LocalTime endTime = startTime.plusHours(duration.getHour()).plusMinutes(duration.getMinute());

            // Check if the end time is on the next day
            boolean isNextDay = endTime.isBefore(startTime);

            // If the end time is the next day, increment the start date by one day
            if (isNextDay) {
                startDate = startDate.plusDays(1);
            }

            // Update the label with the end date and time
            endDateTimeLbl.setText(dateFormatter.format(startDate) + " " + endTime);
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
        hoursSpinner.setValueFactory(
                isAdd ? new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23) :
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                0, 23, selectedShowing.getStartDateTime().getHour()
                        )
        );

        minutesSpinner.setValueFactory(
                isAdd ? new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59) :
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                0, 59, selectedShowing.getStartDateTime().getMinute()
                        )
        );
    }

    private void fillShowingData() {
        titleTextField.setText(selectedShowing.getTitle());
        startDatePicker.setValue(selectedShowing.getStartDateTime().toLocalDate());
        durationTextField.setText(String.valueOf(selectedShowing.getDuration()));
        updateEndDateTimeLabel();
    }

    private void setListeners() {
        titleTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            showTitleError(false);
        });

        durationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            showDurationError(false);
            updateEndDateTimeLabel();
        });

        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            showStartDateError(false);
            updateEndDateTimeLabel();
        });

        hoursSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateEndDateTimeLabel();
        });

        minutesSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateEndDateTimeLabel();
        });
    }
}
