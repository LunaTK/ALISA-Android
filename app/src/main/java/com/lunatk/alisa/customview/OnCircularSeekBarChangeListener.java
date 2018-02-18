package com.lunatk.alisa.customview;

public interface OnCircularSeekBarChangeListener {

    /**
     * Notification that the progress level has changed.
     * Clients can use the fromUser parameter to distinguish user-initiated changes
     * from those that occurred programmatically.
     *
     * @param circularBar : The CircularBar whose progress has changed
     * @param progress    : The current progress level. This will be in the range min.
     * @param fromUser    : True if the progress change was initiated by the user.
     */
    void onProgressChanged(CircularProgressBar circularBar, int progress, boolean fromUser);

    /**
     * User click on circular bar
     *
     * @param circularBar : The CircularBar in which click happen
     */
    void onClick(CircularProgressBar circularBar);

    /**
     * User long click on circular bar
     *
     * @param circularBar : The CircularBar in which long gesture happen
     */
    void onLongPress(CircularProgressBar circularBar);
}