从 Android 6.0（API level 23）开始，用户对应用权限进行授权是发生在应用运行时，而不是在安装时。这样可以让用户在安装时节省时间，而且可以更方便的控制应用的权限（至少权限管理不需要 root 了）。用户可以按照对应用的需求来控制应用的权限，比如百度地图的联系人权限。同时用户也可以在应用程序设置中撤销对应用的权限授权。  
>Android 系统中的权限被划分为两类：`普通权限`和`敏感权限`     
>
>* 普通权限不会涉及到用户隐私，如果应用在 manifest 文件中直接声明了普通权限，系统会自动授予权限给应用。比如：网络`INTERNET`、蓝牙`BLUETOOTH`、震动`VIBRATE`等权限。了解更多：*[普通权限](http://developer.android.com/intl/zh-cn/guide/topics/security/normal-permissions.html)*
>* 敏感权限则要获取到一些用户私密的信息。如果你的应用需要获取敏感权限，首先需要获取用户的授权。比如：相机`CAMERA`、联系人`CONTACTS`、存储设备`STORAGE`了解更多：*[敏感权限](http://developer.android.com/intl/zh-cn/guide/topics/security/permissions.html#perm-groups)*
>
>关于普通权限和敏感权限：*[普通权限及敏感权限](http://developer.android.com/intl/zh-cn/guide/topics/security/permissions.html#normal-dangerous)*    
  

在 Android 的各个版本中，不论是普通权限还是敏感权限，都需要在 manifest 文件中声明，例如*[权限声明](http://developer.android.com/intl/zh-cn/training/permissions/declaring.html)*。然而，在不同版本的操作系统或不同的 target SDK level 中的结果是不同的。  

* 如果设备运行 Android 5.1 或者更低版本的操作系统，或者你的目标 SDK 版本号小于或等于 22，当你在 manifest 文件中请求了一些权限，用户必须在安装过程时授予全部权限，否则应用不能正常安装。
* 如果设备运行在 Android 6.0 或者更高版本，并且目标 SDK 版本号大于或等于23，应用程序必须要在 manifest 文件中声明需要的权限，当程序运行时，它必须要向用户请求授权每个所需的敏感权限。用户可以允许或拒绝每个权限，并且程序可以依赖用户已经授权的权限继续运行。*（这里可能比较绕，举个例子：假设你的 app 需要联系人和拍照权限，在请求权限时用户只授予了联系人权限，那么当前程序可以正常运行并获取联系人信息，但是无法进行拍照）*  

>注：本篇文章讲解如何在 API level 23 或更高版本并且设备版本为 Android 6.0 或者更高。如果 app 的 [`targetSdkVersion`](http://developer.android.com/intl/zh-cn/guide/topics/manifest/uses-sdk-element.html#target) 为 22 或者更低，系统会在安装或者更新程序时提示用户授权所有敏感权限。

本篇文章将会使用 Android [Support Library](http://developer.android.com/intl/zh-cn/tools/support-library/index.html) 检查和请求权限，Andorid 框架在 Android 6.0 (API level 23) 也提供了相似的方法。不过用 support library 不需要检查 Android 的版本，会方便一些。  

## 检查权限  
如果你的程序需要敏感权限，那么你必须在每次调用需要该权限的方法时都需要检查权限。因为用户随时都可能会对你程序的某些权限取消授权，所以即使你的应用昨天使用过相机，你也无法确定今天是否还有这个权限。  
你可以通过[`ContextCompat.checkSelfPermission()`](http://goo.gl/mXQONR)方法来验证你的应用是否拥有某个权限。比如，下面的代码段是检查是否有拥有写日历权限：
  
```java
// 假设 thisActivity 是当前的 Activity
int permissionCheck = ContextCompat.checkSelfPermission(thisActivity,
        Manifest.permission.WRITE_CALENDAR);
```    

如果该应用已经获取到该权限，该方法返回[`PackageManager.PERMISSION_GRANTED`](http://developer.android.com/intl/zh-cn/reference/android/content/pm/PackageManager.html#PERMISSION_GRANTED)并且程序可以继续运行。如果该应用未被授予该权限，这个方法会返回[`PERMISSION_DENIED`](http://developer.android.com/intl/zh-cn/reference/android/content/pm/PackageManager.html#PERMISSION_DENIED)，同时应用需要明确提示用户该应用所需要的权限。  
  
## 请求权限  
如果你的应用需要敏感权限并且这些敏感权限已经在 manifest 文件中声明，一定要询问用户获取权限。Android 系统提供了几种请求权限的方法。调用这些方法后，系统会弹出一些 Dialog（无需用户自定义）。  
  
### 解释需要权限的原因  
在一些应用场景下，你可能想要让用户知道需要获取某个权限的原因。例如，如果用户使用`相册应用`，用户可能会理解这个应用会需要`相机`权限，但是用户可能不会理解为什么相册应用还需要获取`位置`或者`联系人`。在你请求获取权限之前，你应该考虑提示用户。切记不要使用大量解释；如果你解释的内容过多，用户可能会觉得你的应用比较烦人，可能会卸载你的应用...(这段翻译可能有点问题...)  

如果你需要的权限已经被用户拒绝过一次权限请求，当用户再次使用需要获取权限的功能时，应用程序最好向用户解释需要对应权限的原因。因为如果用户一直尝试使用需要权限的功能，却一直没给为该功能对应的权限，说明用户还没有明白为什么应用程序需要这个权限来实现这个功能。在这种情况下可能需要提示用户需要权限的原因。  

>译者注：解释一下上面的例子，比如你的应用程序需要拍照，在用户首次点击`拍照`时是不需要向用户解释为什么需要`CAMERA`权限的，假设用户第一次拒绝向程序授权拍照权限，那么在他下一次点击拍照时，由于应用程序没有权限，那么我们就需要先向用户解释需要`CAMERA`权限的原因（因为用户可能没有理解为什么拍照需要`CAMEAR`权限），然后向用户请求授权`CAMEAR`权限。  

>我理解的就是第一次请求权限时不要向用户解释权限的原因，如果用户拒绝了对应的权限后还需要对应的功能，这时需要向用户解释需要对应权限的原因。尽量不多次解释，以免打扰用户。

Android 系统提供了[`shouldShowRequestPermissionRationale()`](http://dwz.cn/2a1H2M)方法来帮助开发者判断是否需要向用户解释需要权限的原因。当某条权限之前已经请求过，并且用户已经拒绝了该权限时，`shouldShowRequestPermissionRationale ()`方法返回的是 `true`    

>注意：如果用户拒绝某条权限，并且在提示授权的窗口中勾选了`不再提示`选项时，`shouldShowRequestPermissionRationale ()`的返回值为`false`.当某些设备禁止应用程序获取某些权限时，`shouldShowRequestPermissionRationale ()`也会返回`false`  
  
### 向用户请求获取应用程序需要的权限  
如果你的应用程序没有获取到它需要的权限，那么应用程序需要调用该权限对应的[`requestPermissions()`](http://dwz.cn/2a1RST)方法，调用`requestPermissions()`方法时需要传入一个请求码（`requestCode`），这时系统会弹出一个对话框让用户选择是否授权，用户选择后，在回调方法`onRequestPermissionsResult()`中返回对应的请求码（`requestCode`）和授权结果。

下面这段代码 检查应用程序是否有`读联系人`权限，在未获取`读联系人`授权时请求获取该权限(完整示例见*[Android_M_Permission](https://github.com/kingideayou/Android_M_Permission)*)：    


```java
// thisActivity 为当前 Activity
// 检查是否已经授权该权限
if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED) {

    // 判断是否需要解释获取权限原因
    if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
            Manifest.permission.READ_CONTACTS)) {
        // 需要向用户解释
        // 此处可以弹窗或用其他方式向用户解释需要该权限的原因
    } else {
        // 无需解释，直接请求权限
        ActivityCompat.requestPermissions(thisActivity,
                new String[]{Manifest.permission.READ_CONTACTS},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        // MY_PERMISSIONS_REQUEST_READ_CONTACTS 是自定义的常量，在回调方法中可以获取到
    }
}
```  

> 注意：当应用程序调用`requestPermissions()`方法时，系统会弹出一个对话框给用户。应用程序不能设置或更改该对话框，如果应用程序需要提供一些信息或者向用户解释，需要在调用`requestPermissions()`方法之前，如[`Explain why the app needs permissions`](http://developer.android.com/intl/zh-cn/training/permissions/requesting.html#explain)所述。  
  
### 处理请求权限的结果  
当应用程序请求获取权限时，系统会弹出一个对话框给用户。当用户点击某个选项时，系统会调用[`onRequestPermissionsResult()`](http://dwz.cn/2a2eAw)方法来传递用户的选择结果。应用程序需要重写`onRequestPermissionsResult()`方法来判断用户是否对相应权限授权。。这个回调方法会传递一个与`requestPermission()`方法相同的`requestCode`.例如，应用程序请求`READ_CONTACTS`方法，它将会有如下的回调方法：  

```
@Override
public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
    switch (requestCode) {
        case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
            //如果请求被取消，那么 result 数组将为空
            if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 已经获取对应权限
                // TODO 执行相应的操作联系人的方法
            } else {
                // 未获取到授权，取消需要该权限的方法
            }
            return;
        }
        // 检查其他权限......
    }
}
```

授权的对话框显示的是系统描述的权限组（[`permission group`](http://developer.android.com/intl/zh-cn/guide/topics/security/permissions.html#perm-groups)），它没有显示列出详细的权限列表。比如，如果你请求`READ_CONTACTS`权限，系统对话框只会提示用户应用程序需要`获取联系人权限`，用户只需要给每个`权限组`授权一次。如果应用程序请求获取一个权限组的其他权限（在 manifest 文件中声明的权限），系统会自动授予该权限。当你请求这个权限时，系统会调用[`onRequestPermissionsResult()`](http://dwz.cn/2a2eAw)回调方法并且传递`PERMISSION_GRANTED`，这跟用户在弹窗中点击授予权限的按钮的流程是相同的。   

>注意：应用程序还是需要明确的请求它所需要的每个权限，即使用户已经授予了跟这个权限在同一个`permission group`的其他权限。除此之外，对某个权限组的授权可能会改变。程序的代码不能依赖于用户已经对某个权限组授权的假设。

例如，应用程序在 manifest 文件用声明了`READ_CONTACTS`和`WRITE_CONTACTS`权限，如果应用程序请求了`READ_CONTACTS`权限并且用户授予了该权限，那么当应用程序请求`WRITE_CONTACTS`权限时，系统会自动授予应用程序该权限。
>译者注：`READ_CONTACTS`和`WRITE_CONTACTS`都属于`CONTACTS`权限组。更多关于权限组信息可以访问[`permission group`](http://developer.android.com/intl/zh-cn/guide/topics/security/permissions.html#perm-groups)或直接查看页面下方图片  

如果用户拒绝了一个应用权限请求，那么应用程序应该进行适当的操作。例如：应用程序可以弹出一个对话框来解释为什么用户不能执行需要该权限的操作。  
 
当系统提示用户给应用程序授权权限时，会给用户提供一个`不再提示`的选项来通知系统不再针对该权限进行询问。用户勾选该选项后，当应用程序请求获取对应权限时，系统会立即拒绝授权。系统会调用`onRequestPermissionResult()`回调方法并且传递`PERMISSION_DENIED`参数，就像用户拒绝授权一样。这意味着，当你调用`requestPermissions()`方法时，你不能假定应用程序会跟用户直接交互。  


***  


原文地址：[Requesting Permissions at Run Time](http://developer.android.com/intl/zh-cn/training/permissions/requesting.html)  
获取权限 Demo 地址：[Android_M_Permission](https://github.com/kingideayou/Android_M_Permission)  
获取权限并拍照 Demo 地址：[Android_M_Capturing_Pic](https://github.com/kingideayou/Android_M_Capturing_Pic)  

如果你觉得对权限进行验证比较麻烦，这里有个开源库可能会帮你减少一些操作：[AndroidPermissions](
https://github.com/ZeroBrain/AndroidPermissions)  
