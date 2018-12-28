import * as functions from 'firebase-functions';

// Called when a document gets added to 'sharedDocuments'
// This function will add the userId to the owner documents 'userIds' field
/*export const onAddSharedDocument = functions.database.ref('/sharedDocuments/{userId}/{docId}').onCreate((snapshot, context) => {
    

})*/

// Called when a document in 'sharedDocuments' is updated
// This function will update the owner document
export const updateOnSharedDocument = functions.database.ref('/sharedDocuments/{userId}/{docId}').onUpdate((change, context) => {
    const document = change.after.val()
    const docId = context.params.docId
    const ownerId = document.ownerId
    const updates = []

    if (change.before.val() === change.after.val()) return null
    updates.push(change.before.ref.root.child(`createdDocuments/${ownerId}/${docId}`).set(document))
    updates.push(change.after.ref.set(document))

    return Promise.all(updates)
})

// Called when a document in 'createdDocuments' is updated
// This function will update all documents in 'sharedDocuments' which are linked in the 'userIds' field
export const updateOnCreatedDocuments = functions.database.ref('/createdDocuments/{userId}/{docId}').onUpdate((change, context) => {
    const document = change.after.val()
    const docId = context.params.docId
    const updates = []

    if (change.before.val() === change.after.val()) return null

    change.before.val().userIds.forEach(userId => {
        updates.push(change.before.ref.root.child(`sharedDocuments/${userId}/${docId}`).set(document))
    });

    updates.push(change.before.ref.set(document))

    return Promise.all(updates)
})
