package com.trykote.mobileagent.ui;

import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.net.ApiClient;

/**
 * Base class for async loading screens (show indicator, send request, poll for result).
 * The screen stays in the stack with its real screenId so onIdle() is dispatched correctly.
 */
public abstract class AsyncScreenView extends ScreenView {

    private final int loadingIcon;
    private final int loadingText;

    protected AsyncScreenView(int screenId, int loadingIcon, int loadingText) {
        super(ScreenManager.TYPE_TOAST, screenId);
        this.loadingIcon = loadingIcon;
        this.loadingText = loadingText;
    }

    public void buildContent() {
        UIState.setHttpParam1(loadingIcon);
        UIState.setHttpParam2(loadingText);
        configureSoftKeys(null, 0, "Отмена", 12, 0);
    }

    public void showSelf() {
        startRequest();
        ScreenManager.showScreen(this);
    }

    public abstract void startRequest();

    public abstract int processResponse(Object[] result);

    public int onItemSelected(MenuItem item, String title, int action, Object data) {
        return -1;
    }

    public int onSelect(MenuItem item, String title, int selectedOption,
                        Object data, Object headerData) {
        return -1;
    }

    public int onIdle(MenuItem item, Object data, String title) {
        Object[] result = ApiClient.getAsyncResult(ApiClient.pollAsyncResult());
        if (result == null) {
            return 0;
        }
        return processResponse(result);
    }
}
