import com.github.kennedyoliveira.pastebin4j.AccountCredentials;
import com.github.kennedyoliveira.pastebin4j.Paste;
import com.github.kennedyoliveira.pastebin4j.PasteBin;

import java.util.List;

/**
 * @author kennedy
 */
public class DeletePasteExample {

    public static void main(String[] args) {
        final String devKey = "dev-key";
        final String userName = "user-name";
        final String password = "password";

        final PasteBin pasteBin = new PasteBin(new AccountCredentials(devKey, userName, password));

        // List the pastes
        final List<Paste> pastes = pasteBin.listUserPastes();

        if (pastes.isEmpty()) {
            System.out.println("You have no pastes to delete :(");
            return;
        }

        // Get the one you wanna delete
        final Paste firstPaste = pastes.get(0);

        final boolean deleted = pasteBin.deletePaste(firstPaste);

        if (deleted) {
            System.out.println("The paste was deleted succefully!");
        }
    }
}
