package model;

import java.sql.SQLException;

public class RegisterValidator {
    private String idInput;
    private String nameInput;
    private String pwInput;
    private String phoneNumber;
    private String imageSource;
    private char gender;

    private Database db;

    public RegisterValidator(String idInput, String nameInput, String pwInput, String phoneNumber, String imageSource, char gender) {
        this.idInput = idInput;
        this.nameInput = nameInput;
        this.pwInput = pwInput;
        this.phoneNumber = phoneNumber;
        this.imageSource = imageSource;
        this.gender = gender;

        db = new UserDB();
        db.connect();
    }

    private boolean isValidId(String id) {
        boolean valid = true;
        BannedListDB bandb = new BannedListDB();
        bandb.connect();
        bandb.selectAll();

        try {
            while (bandb.rs.next()) {
                String bannedWord = bandb.rs.getString("word");
                if (id.contains(bannedWord)) {
                    valid = false;
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            bandb.closeDB();
        }
        return valid;
    }

    private boolean isValidPw(String pw) {
        if (!hasUpperCase(pw)) {
            return false;
        }
        if (!hasNumbers(pw)) {
            return false;
        }
        if (!hasSpecialCharacter(pw)) {
            return false;
        }
        if (pw.length() < 8) {
            return false;
        }

        return true;
    }

    private boolean hasUpperCase(String str) {
        return !str.equals(str.toLowerCase());
    }

    private boolean hasNumbers(String str) {
        return str.matches(".*\\d+.*");
    }

    private boolean hasSpecialCharacter(String str) {
        return !str.matches("[A-Za-z0-9 ]*");
    }

    private boolean isValidHp(String hp) {
        if (!hp.substring(0, 3).equals("010")) {
            return false;
        }
        if (hp.length() != 11) {
            return false;
        }
        return true;
    }

    private boolean isDuplicate(String id) {
        db.selectAll();
        try {
            while (db.rs.next()) {
                String storedID = db.rs.getString("ID");
                if (id.equals(storedID)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean confirm() {
        if (!isValidId(this.idInput)) {
            return false;
        }
        if (isDuplicate(this.idInput)) {
            return false;
        }
        if (!isValidHp(this.phoneNumber)) {
            return false;
        }
        if (!isValidPw(this.pwInput)) {
            return false;
        }

        //automatically logs in after registration succeeds
        db.insert(this);

        db.closeDB();
        return true;
    }

    public String getIdInput() {
        return idInput;
    }

    public String getNameInput() {
        return nameInput;
    }

    public String getPwInput() {
        return pwInput;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getImageSource() {
        return imageSource;
    }

    public char getGender() {
        return gender;
    }
}
