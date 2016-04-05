package ca.ubc.cpen391team17; /**
 * Class used to store the state of the checkboxes
 */

import java.io.Serializable;

class CheckboxesState implements Serializable {
    private static final long serialVersionUID = 2L;
    public boolean checkbox1;
    public boolean checkbox2;
    public boolean checkbox3;
    public boolean checkbox4;

    public CheckboxesState() {
        checkbox1 = false;
        checkbox2 = false;
        checkbox3 = false;
        checkbox4 = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CheckboxesState)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        CheckboxesState c = (CheckboxesState) obj;
        return this.checkbox1 == c.checkbox1 &&
                this.checkbox2 == c.checkbox2 &&
                this.checkbox3 == c.checkbox3 &&
                this.checkbox4 == c.checkbox4;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}