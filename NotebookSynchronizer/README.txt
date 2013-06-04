CIS542 Spring 2012 NotebookSynchronizer
Name: Yayang Tian

An Android notebook synchronizer like Evernote where users users create, view, and modify, and synchronize your notes in terms of date, title, tag, and content.

SDK and Eclipse are used in ENIAC Machine
Components:
ContentProvider: it stores the data.
A SyncAdapter: it communicates with a remote server to obtain data to put into the ContentProvider.
The Android ContentResolver: which figures out how to pair up SyncAdapters and ContentProviders. 