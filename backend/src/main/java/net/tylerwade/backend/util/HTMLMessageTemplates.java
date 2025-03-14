package net.tylerwade.backend.util;

public class HTMLMessageTemplates {

    public static String getVerificationCodeTemplate(String code) {
        return "<div style=\"width: 100%; height: 100vh; background: #f8fafc; color: #0f172a; display: flex; flex-direction: column; justify-content: center; align-items: center;\">\n" +
                "        <div style=\"width: 100%; max-width: 30rem; height: fit-content; background: white; padding: 2rem; border-radius: 1rem; box-shadow: 0 8px 12px 0px #000;\">\n" +
                "            <h1 style=\"text-align: center; color: #3b82f6;\">Vax Monitor | API Monitoring</h1>\n" +
                "            <hr style=\"border: 1px solid #64748b; width: 100%\">\n" +
                "            <h2 style=\"text-align: center;\">Your Verification Code is:</h2>\n" +
                "            <p style=\"text-align: center; color: #3b82f6; font-size: 2rem; font-weight: bold;\">" + code + "</p>\n" +
                "            <hr style=\"border: 1px solid #64748b; width: 100%\">\n" +
                "            <p style=\"text-align: center;\">If you did not request a verification code, please ignore this email.</p>\n" +
                "        </div>\n" +
                "    </div>";
    }

    public static String getDeleteAccountVerificationCodeTemplate(String code) {
        return "<div style=\"width: 100%; max-width: 30rem; height: fit-content; background: white; padding: 2rem; border-radius: 1rem; box-shadow: 0 8px 12px 0px #000;\">\n" +
                "        <h1 style=\"text-align: center; color: #3b82f6;\">Vax Monitor | API Monitoring</h1>\n" +
                "        <hr style=\"border: 1px solid #64748b; width: 100%\">\n" +
                "        <p style=\"text-align: center;\">You have requested to delete your account.</p>\n" +
                "        <h2 style=\"text-align: center;\">Your Verification Code is:</h2>\n" +
                "        <p style=\"text-align: center; color: #3b82f6; font-size: 2rem; font-weight: bold;\">" + code + "</p>\n" +
                "        <hr style=\"border: 1px solid #64748b; width: 100%\">\n" +
                "        <p style=\"text-align: center;\">If you did not request a verification code, please ignore this email and change your password.</p>\n" +
                "    </div>";
    }

}
