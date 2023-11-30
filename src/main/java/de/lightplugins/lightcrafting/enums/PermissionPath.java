package de.lightplugins.lightcrafting.enums;

public enum PermissionPath {

    /*
        Admin Command Perissions
     */

    RELOAD("lightcrafting.admin.reload"),

    /*
        User Command Perissions
     */

    PayCommand("lighteconomy.user.command.pay"),
    MoneyTop("lighteconomy.user.command.top"),
    CreateVoucher("lighteconomy.user.command.createvoucher"),
    BankOpen("lighteconomy.user.command.bank"),
    ;

    private final String path;
    PermissionPath(String path) { this.path = path; }
    public String getPerm() {
        return path;
    }
}
