package moe.lyrebird.view.screens.login;

public enum LoginSteps {
    STEP_1(1),
    STEP_2(2),
    STEP_3(3);

    private final int stepNumber;

    LoginSteps(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public int getStepNumber() {
        return stepNumber;
    }
}
