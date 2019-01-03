import * as functions from 'firebase-functions';

// Called when a document gets added to 'sharedDocuments'
// This function will add the userId to the owner documents 'userIds' field
export const createOnSharedDocuments = functions.database.ref('/sharedDocuments/{userId}/{docId}').onCreate((data, context) => {
    // TODO: add user id to createdDocuments userIds
    const document = data.val();
    const userId = context.params.userId;
    const docId = context.params.docId;
    const ownerId = document.ownerId;

    return data.ref.root.child(`createdDocuments/${ownerId}/${docId}`).once('value').then(snap => {
        const snapData = snap.val();
        if (!snapData.userIds) {
            snapData.userIds = [];
        }
        snapData.userIds.push(userId);

        return snap.ref.set(snapData);
    });
});

// Called when a document in 'sharedDocuments' is updated
// This function will update the owner document
export const updateOnSharedDocuments = functions.database.ref('/sharedDocuments/{userId}/{docId}').onUpdate((change, context) => {
    const document = change.after.val();
    const docId = context.params.docId;
    const ownerId = document.ownerId;

    if (change.before.val() === change.after.val()) return null;

    return change.before.ref.root.child(`createdDocuments/${ownerId}/${docId}`).set(document);
});

export const deleteOnSharedDocuments = functions.database.ref('/sharedDocuments/{userId}/{docId}').onDelete((data, context) => {
    const document = data.val();
    const docId = context.params.docId;
    const ownerId = document.ownerId;

    // Filter document's user ids for the user that removed its shared document node
    document.userIds = document.userIds.filter(id => id !== context.params.userId);

    return data.ref.root.child(`createdDocuments/${ownerId}/${docId}`).set(document);
});

// Called when a document in 'createdDocuments' is updated
// This function will update all documents in 'sharedDocuments' which are linked in the 'userIds' field
export const updateOnCreatedDocuments = functions.database.ref('/createdDocuments/{userId}/{docId}').onUpdate((change, context) => {
    const document = change.after.val();
    const docId = context.params.docId;
    const updates = [];

    if (change.before.val() === change.after.val()) return null;

    if(change.after.val().userIds) {
        change.after.val().userIds.forEach(userId =>
            updates.push(change.before.ref.root.child(`sharedDocuments/${userId}/${docId}`).set(document))
        );
    }

    return Promise.all(updates);
});

export const deleteOnCreatedDocuments = functions.database.ref('/createdDocuments/{userId}/{docId}').onDelete((data, context) => {
    const document = data.val();
    const docId = context.params.docId;
    const updates = [];

    if(document.userIds) {
        document.userIds.forEach(userId =>
            updates.push(data.ref.root.child(`sharedDocuments/${userId}/${docId}`).remove())
        );
    }

    return Promise.all(updates);
});
