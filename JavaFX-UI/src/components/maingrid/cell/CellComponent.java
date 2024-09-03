package components.maingrid.cell;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class CellComponent extends StackPane {

    private String identity;      // מזהה התא, לדוגמה "A1", "B2", וכו'
    private Label effectiveValue;    // התווית שתציג את הערך בתא

    public CellComponent(String identity, String effectiveValue) {
        this.identity = identity;
        this.effectiveValue = new Label(effectiveValue);  // יצירת התווית עם הערך הראשוני

        // הוספת התווית ל-StackPane של התא
        this.getChildren().add(this.effectiveValue);

        // אפשרות: הוספת עיצוב בסיסי לתא (גבול, ריווח וכו')
        this.setStyle("-fx-border-color: black; -fx-padding: 10; -fx-alignment: center;");
    }

    // פונקציה לשינוי הערך בתא
    public void setEffectiveValue(String value) {
        effectiveValue.setText(value);
    }

    // פונקציה לקבלת הערך הנוכחי בתא
    public String getEffectiveValue() {
        return effectiveValue.getText();
    }

    // פונקציה לקבלת מזהה התא
    public String getIdentity() {
        return identity;
    }
}

