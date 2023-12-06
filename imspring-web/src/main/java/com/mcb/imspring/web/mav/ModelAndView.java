package com.mcb.imspring.web.mav;

public class ModelAndView {

    private Object view;

    private ModelMap model;

    /**
     * request已经完全被处理，例如@RequestBody方法
     */
    private boolean requestHandled = false;

    public Object getView() {
        return view;
    }

    public void setView(Object view) {
        this.view = view;
    }

    public ModelMap getModel() {
        return model;
    }

    public void setModel(ModelMap model) {
        this.model = model;
    }

    public boolean isRequestHandled() {
        return requestHandled;
    }

    public void setRequestHandled(boolean requestHandled) {
        this.requestHandled = requestHandled;
    }
}
