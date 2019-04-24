package caketpl;

// NavigatorElement represents a Navigator selector fragment.
public class NavigatorElement {
    private String stringElement;
    private int indexElement;

    public NavigatorElement(String stringElement) {
        this.stringElement = stringElement;
    }

    public NavigatorElement(int indexElement) {
        this.indexElement = indexElement;
    }

    public void executeOn(Navigator n) {
        if (this.stringElement != null) {
            n.goKey(this.stringElement);
        } else {
            n.goIndex(this.indexElement);
        }
    }
}
