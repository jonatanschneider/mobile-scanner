import * as functions from 'firebase-functions';

/*export const onAddSharedDocument = functions.database.ref('/sharedDocuments/{userId}/{docId}').onCreate((snapshot, context) => {
    const userId = context.params.userId
    const docId = context.params.docId

    const document = snapshot.val()

    document.name = "Functions work!"
    return snapshot.ref.update(document);
})*/

export const updateOnCreatedDocuments = functions.database.ref('/createdDocuments/{userId}/{docId}').onUpdate((change, context) => {
    const document = change.before.val()
    const docId = context.params.docId
    const updates = []

    document.userIds.forEach(userId => {
        updates.push(change.before.ref.root.child(`sharedDocuments/${userId}/${docId}`).update(change.after.val()))
    });

    updates.push(change.before.ref.update(change.after.val()))
    return Promise.all(updates)
})

export const updateOnSharedDocument = functions.database.ref('/sharedDocuments/{userId}/{docId}').onUpdate((change, context) => {
    const document = change.before.val()
    const docId = context.params.docId
    const ownerId = document.ownerId
    const updates = []

    document.userIds.forEach(userId => {
        updates.push(change.before.ref.root.child(`sharedDocuments/${userId}/${docId}`).update(change.after.val()))
    });

    updates.push(change.before.ref.root.child(`createdDocuments/${ownerId}/${docId}`).update(change.after.val()))

    return Promise.all(updates)
})

