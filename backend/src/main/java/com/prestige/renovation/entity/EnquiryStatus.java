package com.prestige.renovation.entity;

/** Enquiry lifecycle: NEW -> READ -> REPLIED, never back to NEW. */
public enum EnquiryStatus {
    NEW, READ, REPLIED
}
