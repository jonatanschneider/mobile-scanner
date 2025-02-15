# How to contribute


## Getting Started

* Make sure you have a [THM Gitlab account](https://git.thm.de/).
* Submit an issue for every task if it does not already exist.
* Clearly describe the issue including steps to reproduce when it is a bug.

## Making changes

* Create a topic branch from where you want to base your work.
    * This should always be the master branch, unless something went terribly wrong.
    * Use tags to describe your issue/branch/merge request.
    * Always provide information on what you are working on.
    * To quickly create a topic branch, go to your issue, expand `Create a merge request` and select `Create branch`.
* Make commits of logical and atomic units.
* Check for unnecessary whitespaces with `git diff --check` before commiting.
* Create tests for your changes, if possible (yes, it takes time, it is annoying, but also it is **necassary**)
* Run *all* tests to assure nothing else was accidentally broken.

## Submitting Changes

* Push your changes to a topic branch in the repository.
* Check whether your topic branch is up to date with `master`. If not, please rebase your branch.
* Submit a merge request to the repository.
* Provide information about what changed.
* Mark you merge request with `ready for testing` when you finished your work. If you haven't already, mark it with `work in progress` and add `[WIP]: ` to the merge request's title.
* The team will then test your changes. When everything is as expected, your merge request will be marked as `ready for review`
* The scrum master will then check your code for style and compatibility. If everything is okay, your changes will be merged.
* If something goes wrong - do not panic! There will be a change request with more information about what went wrong. You can fix these problems, your merge request will remain open.

## Merge Requests

Merge Requests should have a meaningful title and description. Describe 

* what the issue was (briefly) or what the bug was (briefly)
* how to reconstruate the situation, the issue is linked to
* what the previous behaviour was
* what the expected behaviour is

The description should match the standards in commit messages all over all.

* Use `ready for testing` to mark your merge request as done. Another team member should then checkout this branch and 
test all changes. Please provide a meaningful description to help them review your work. Test for all possibilities and 
eventualities, as far as possible. Test beyond the expected behaviour.
* When you are ready with testing, mark the merge request as `ready for review`. This tag tells the other team members 
that the merge request should (in best case) be bug free. The team members will then make a code review where they 
check whether or not the naming conventions are observed etc.

## Styleguides

### Git Commit Messages

* Use the present tense (`Add feature` instead of `Added feature`).
* Use the imperative mood (`Move cursor to...`instead of `Moves cursor to...`).
* Limit the first line to 72 characters or less.
* Reference issues and merge requests liberally after the first line.

### Branch names

Branch names are found after the following rules:

issue-id-user-story-name-issue-name

E.g. `21-login-screen-project-setup`

They are automatically generated by pressing `Create branch` under the arrow in `Create Merge Request` on the issue 
page.