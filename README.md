# Birthday Reminder 2.0
App for Android devices which notifies users of upcoming birthdays.

![](http://julianrosser.website/images/app_screenshots/birthday180.png)![](http://julianrosser.website/images/app_screenshots/birthday184.png)

TODO - Main:
- Tablet & small Layouts
- Theme, material theme
- TypeFace-Light for api < 16
- Animation

Resources:
- App launcher icon name
- Icon, screenshots, video

Marketing:
- Localization strings
- Deep links
- Analytics

Final:
- Update Version to 2.0
- Refactor Logs, testing code to text file
- Prepare to launch - https://developer.android.com/tools/publishing/preparing.html
- final checks - https://developer.android.com/distribute/tools/launch-checklist.html

Bugs:
- Crash when click noti while on settings activity and go back to 1st (Unlikely to happen to user)
- Quickly click to open Fragments twice
- End of month bug?
- SettingsActivity keeps calling Service. callback or some solution to ToManyCalls problem

Next Update?
- Widget
- 2 birthdays on same day?
- Fragments as bottom cards?
- Custom Notification
- Animation
- SQL database instead of JSON ??? necessary? Will take effort, perhaps as backup


Log
- 18/12 - Added reminder toggle option, icon & functionality.
          Somehow managed to delete build.gradle. CHAOS. Noticed the file was removed from last commit.
- 17/12 - Birthdays now saved and notification alarms set when data changes. FloatingActionButton, Material icon & colours.
          Cancel notification when deleted. Added preferences (Reminder time, vibrate?, sound?, test noti, sorting). New screenshots.
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

