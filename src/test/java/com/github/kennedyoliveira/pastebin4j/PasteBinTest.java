package com.github.kennedyoliveira.pastebin4j;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author kennedy
 */
public class PasteBinTest {

    AccountCredentials accountCredentials;
    PasteBin pasteBin;

    @Before
    public void setUp() throws Exception {
        final String devkey = System.getProperty("pastebin4j.devkey");

        if (devkey == null)
            throw new RuntimeException("Dev key missing, especify the devkey in the running command with -Dpastebin4j.devkey=devkey");

        String username = System.getProperty("pastebin4j.username");

        if (username == null)
            throw new RuntimeException("Username missing, especify the username in the running command with -Dpastebin4j.username=username");

        String password = System.getProperty("pastebin4j.password");

        if (password == null)
            throw new RuntimeException("Password missing, especify the password in the running command with -Dpastebin4j.password=password");

        accountCredentials = new AccountCredentials(devkey, username, password);
        pasteBin = new PasteBin(accountCredentials);
    }

    @Test
    public void testFetchUserInformation() throws Exception {
        final UserInformation userInformation = pasteBin.fetchUserInformation();

        assertThat(userInformation, is(notNullValue()));
        assertThat(userInformation.getUsername(), is(notNullValue()));
        assertThat(userInformation.getAccountType(), is(notNullValue()));
        assertThat(userInformation.getDefaultPasteExpiration(), is(notNullValue()));
        assertThat(userInformation.getDefaultPasteVisility(), is(notNullValue()));
        assertThat(userInformation, is(pasteBin.getUserInformation().get()));
    }

    @Test
    public void testCreatePaste() throws Exception {
        final Paste paste = Paste.newBuilder()
                                 .withTitle("Automated pastebin4j testing")
                                 .withContent(UUID.randomUUID().toString())
                                 .withVisibility(PasteVisibility.PUBLIC)
                                 .withHighLight(PasteHighLight.TEXT)
                                 .withExpiration(PasteExpiration.ONE_MONTH)
                                 .build();

        final String url = pasteBin.createPaste(paste);

        assertThat(url, CoreMatchers.containsString("pastebin.com/"));
    }

    @Test
    public void testListUserPastes() throws Exception {
        testCreatePaste();

        final List<Paste> userPastes = pasteBin.listUserPastes();

        assertThat(userPastes, allOf(notNullValue(), hasSize(greaterThan(0))));
    }

    @Test
    public void testDeletePaste() throws Exception {
        final Paste createdPaste = Paste.newBuilder()
                                        .withTitle("Automated pastebin4j testing")
                                        .withContent(UUID.randomUUID().toString())
                                        .withVisibility(PasteVisibility.PUBLIC)
                                        .withHighLight(PasteHighLight.TEXT)
                                        .withExpiration(PasteExpiration.ONE_MONTH)
                                        .build();

        pasteBin.createPaste(createdPaste);

        final String content = createdPaste.getContent();

        final List<Paste> pastes = pasteBin.listUserPastes();

        final Optional<Paste> paste = pastes.stream().filter(p -> p.fetchContent().equals(content)).findFirst();

        assertThat("Found the paste created", paste.isPresent(), is(true));

        final Paste pasteFound = paste.get();

        final boolean deleted = pasteBin.deletePaste(pasteFound);

        assertThat(deleted, is(true));

        final List<Paste> newUserPastes = pasteBin.listUserPastes();

        boolean foundPasteDeleted = newUserPastes.stream().anyMatch(p -> p.getKey().equals(pasteFound.getKey()));

        assertThat(foundPasteDeleted, is(false));
    }

    @Test
    public void testListTrendingPastes() throws Exception {
        final List<Paste> pastes = pasteBin.listTrendingPastes();

        assertThat(pastes, allOf(notNullValue(), hasSize(18)));
    }

    @Test
    public void testGetPasteContent() throws Exception {
        final String contents = pasteBin.getPasteContent(new Paste("rrPV9pzZ"));

        assertThat(contents, is("test for pastebin4j api"));
    }

    @Test
    public void test_get_account_credentials() throws Exception {
        assertThat(pasteBin.getAccountCredentials(), is(this.accountCredentials));
    }
}