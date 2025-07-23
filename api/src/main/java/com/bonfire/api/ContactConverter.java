package com.bonfire.api;

import lombok.experimental.UtilityClass;
import lombok.val;
import com.bonfire.contacts.AddressBookEntry;
import com.bonfire.contacts.Contact;
import com.bonfire.contacts.ImportedContact;
import com.bonfire.internal.api.services.InputContact;

import java.util.List;

@UtilityClass
public class ContactConverter {

    public Contact toPublic(com.bonfire.internal.api.models.Contact contact) {
        return Contact.newBuilder()
                .setUserId(contact.getUserId())
                .setName(contact.getName())
                .build();
    }

    public List<InputContact> toInputInternal(List<com.bonfire.contacts.InputContact> inputContacts) {
        return inputContacts.stream()
                .map(ic -> InputContact.newBuilder()
                        .setClientId(ic.getClientId())
                        .setPhoneNumber(ic.getPhoneNumber())
                        .setName(ic.getName())
                        .build())
                .toList();
    }

    public List<ImportedContact> toImportedPublic(List<com.bonfire.internal.api.services.ImportedContact> importedContacts) {
        return importedContacts.stream()
                .map(ic -> {
                    val result = ImportedContact.newBuilder();
                    result.setClientId(ic.getClientId());
                    if (ic.hasUserId()) result.setUserId(ic.getUserId());
                    return result.build();
                })
                .toList();
    }

    public List<AddressBookEntry> toAddressBookPublic(List<com.bonfire.internal.api.models.AddressBookEntry> entries) {
        return entries.stream()
                .map(entry -> AddressBookEntry.newBuilder()
                        .setName(entry.getName())
                        .setPhoneNumber(entry.getPhoneNumber())
                        .setEditedAt(entry.getEditedAt())
                        .build())
                .toList();
    }
}
