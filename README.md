# Birthday Reminder 2.0
App for Android devices which notifies users of upcoming birthdays.

![](http://julianrosser.website/images/app_screenshots/birthday15.png)![](http://julianrosser.website/images/app_screenshots/birthday16.png)

TODO
- FloatingActionButton to replace ActionButton on ?API? +
- Save data when changed
- Call service when Activity is stopped,  (Delay service so Data is ALWAYS saved first)
- If (DAY/TIME) settings changed, reset ALL alarms
- Alarm on?

- Tablet Layouts
- Localization
- Deep linking
- Old APIs button highlight
- Analytics
- Reminder on??? bool needed? YES, as option
- TypeFace-Light for api < 16
- 2 birthdays on same day?
- App launcher icon name
- Icon, screenshots, video
- Remove Logs
- Refactor testing code to text file
- Prepare to launch - https://developer.android.com/tools/publishing/preparing.html
- final checks - https://developer.android.com/distribute/tools/launch-checklist.html

Next Update?
- Widget
- Fragments as bottom cards?
- Custom Notification
- Animation
- SQL database instead of JSON ??? necessary? Will take effort, perhaps as backup

Bugs:
- Crash when click noti while on settings activity and go back to 1st (Unlikely to happen to user)
- Quickly click to open Fragments twice
- End of month bug?

Log
- 17/12 - Cancel notification when deleted. Added preferences (Reminder time, vibrate?, sound?, test noti, sorting)
- 16/12 - Built custom Adapter so ItemOptionFrag shows Icons. Save/recall Fragment references onRotate.
          Dialogs theme & size. Replaced unnecessary Activity context refs with AppContext. Refactored Arrays.
          Created birthday_notification.mp3 & added to notification. Settings theme & Time preference
- 15/12 - Created NotificationBuilder class and added personalised notifications. Cake app icon. Vibrate pattern.
        - SettingActivity & Fragment. Works with days before remind setting. Reduced DatePicker padding.
- 14/12 - Auto sorting. Refactored LoadBirthdaysTask.
- 12/12 - Service is launched on boot. Created BootNotificationReceiver and SetAlarmService.
- 10/12 - Prevented context leaks. Built date/day remaining helper methods. RobotoLight font.
Custom date bg drawables with elevation. Now editing birthday instead of replacing. Updated screenshots.
- 09/12 - Saving/loading using lifecycle methods. New ItemMenuFragment to replace context hack. Rewrote BirthdayListView
          to Material-specs. Formatted and documented Adapter & Fragment.
- 08/12 - Added JSON save/load functions.
- 07/12 - Orientation change functionality. Adapter updates list correctly using UI Thread. Context menu (del, edit). Formatting & Documentation
- 06/12 - Add birthday (or edit) DialogFragment, layout & functionality. Fragment callback listener. Icon for menu_add.
- 04/12 - First real progress to redesign. Re-wrote Birthdays class, RecyclerView & Adapter now working correctly.
- 03/12 - Birthday list (RecyclerViewFragment) displays correctly. Rewrote Birthdays.java.
- 20/11 - Created RecyclerViewAdapter, converted to use Array of Birthdays. P
- 18/10 - Designed new UX on paper.
- 03/09 - Redesigned, formatted and documented Birthdays.java.
- 02/09/2015 - Built new AS project. Pushed to new GitHub repo.

