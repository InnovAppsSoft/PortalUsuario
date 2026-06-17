# ============================================================
# Hilt ViewModels
# ============================================================
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# ============================================================
# SuiteTecsa SDK
# ============================================================
-keep class io.github.suitetecsa.sdk.** { *; }

# ============================================================
# Room
# ============================================================
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class com.marlon.portalusuario.data.entity.Converters { *; }

# ============================================================
# Gson — classes serialized via @SerializedName or TypeConverters
# ============================================================
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.marlon.portalusuario.domain.model.DataSession { *; }
-keep class com.marlon.portalusuario.domain.model.SimPaired { *; }
-keep class com.marlon.portalusuario.domain.model.SlotIndexInfo { *; }
-keep class com.marlon.portalusuario.domain.model.MobileBonus { *; }
-keep class com.marlon.portalusuario.domain.model.MobilePlan { *; }
-keep class com.marlon.portalusuario.domain.model.MobServPreferences { *; }

# ============================================================
# AndroidManifest components
# ============================================================
-keep class com.marlon.portalusuario.activities.MainActivity { *; }
-keep class com.marlon.portalusuario.permisos.PermissionActivity { *; }
-keep class com.marlon.portalusuario.punotifications.PUNotificationsActivity { *; }
-keep class com.marlon.portalusuario.erroreslog.LogFileViewerActivity { *; }
-keep class com.marlon.portalusuario.feature.splash.presentation.ActivitySplash { *; }
-keep class com.marlon.portalusuario.firebase.FirebaseService { *; }
-keep class com.marlon.portalusuario.trafficbubble.FloatingBubbleService { *; }
-keep class com.marlon.portalusuario.widgets.Widget { *; }
-keep class com.marlon.portalusuario.widgets.WidgetDark { *; }
-keep class com.marlon.portalusuario.widgets.WidgetUSSD { *; }

# ============================================================
# JWT Decode
# ============================================================
-keep class com.auth0.android.jwt.** { *; }

# ============================================================
# Glide
# ============================================================
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class com.bumptech.glide.** { *; }
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# ============================================================
# PrettyTime
# ============================================================
-keep class org.ocpsoft.prettytime.** { *; }

# ============================================================
# DataStore preferences
# ============================================================
-keep class com.marlon.portalusuario.data.preferences.** { *; }

# ============================================================
# Hilt / Dagger
# ============================================================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# ============================================================
# Enums
# ============================================================
-keepclassmembers enum com.marlon.portalusuario.** { *; }

# ============================================================
# Navigation Compose — NavType argument serialization
# ============================================================
-keep class androidx.navigation.NavType { *; }

# ============================================================
# Application class
# ============================================================
-keep class com.marlon.portalusuario.util.PortalUsuarioApplication { *; }
