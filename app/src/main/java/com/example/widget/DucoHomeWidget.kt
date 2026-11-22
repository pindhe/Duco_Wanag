package com.example.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.semantics.contentDescription
import androidx.glance.semantics.semantics
import androidx.glance.semantics.testTag
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.MainActivity
import com.example.R
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DucoHomeWidgetReceiver : GlanceAppWidgetReceiver() {
  override val glanceAppWidget: GlanceAppWidget = DucoHomeWidget()
}

class IncrementDucoWidgetAction : ActionCallback {
  override suspend fun onAction(
    context: Context,
    glanceId: GlanceId,
    parameters: ActionParameters,
  ) {
    DucoWidgetDataSource.incrementActiveDucoFromWidget(context)
  }
}

class DucoHomeWidget : GlanceAppWidget() {

  override val sizeMode: SizeMode = SizeMode.Responsive(RESPONSIVE_BUCKETS)

  override suspend fun provideGlance(context: Context, id: GlanceId) {
    val initialSnapshot =
      withContext(Dispatchers.IO) { DucoWidgetDataSource.loadSnapshot(context) }
    val sharedBgBitmap =
      withContext(Dispatchers.IO) { decodeWidgetBackgroundBitmap(context) }

    provideContent {
      val snapshot by
        remember(context) { DucoWidgetDataSource.observeSnapshot(context) }
          .collectAsState(initial = initialSnapshot)

      val systemNight =
        (LocalContext.current.resources.configuration.uiMode and
          Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
      val isDark = snapshot.isDarkModePreference ?: systemNight

      DucoWidgetContent(
        snapshot = snapshot,
        isDark = isDark,
        size = LocalSize.current,
        backgroundBitmap = sharedBgBitmap,
      )
    }
  }

  override fun onCompositionError(
    context: Context,
    glanceId: GlanceId,
    appWidgetId: Int,
    throwable: Throwable,
  ) {
    val fallback =
      RemoteViews(context.packageName, android.R.layout.simple_list_item_1).apply {
        setTextViewText(android.R.id.text1, context.getString(R.string.app_name))
      }
    AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, fallback)
  }

  companion object {
    private val RESPONSIVE_BUCKETS =
      setOf(
        DpSize(180.dp, 96.dp),
        DpSize(250.dp, 110.dp),
        DpSize(340.dp, 140.dp),
      )

    internal fun decodeWidgetBackgroundBitmap(context: Context): Bitmap? {
      return runCatching {
        val metrics = context.resources.displayMetrics
        val density = max(1f, metrics.density)
        val reqWidth = (260f * density).roundToInt().coerceAtLeast(120)
        val reqHeight = (160f * density).roundToInt().coerceAtLeast(80)
        val screenCapBytes = metrics.widthPixels.toLong() * metrics.heightPixels.toLong() * 6L
        val maxBytes = minOf(screenCapBytes / 4L, 768L * 1024L).coerceAtLeast(128L * 1024L)

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeResource(context.resources, R.drawable.img_duco_card_bg, bounds)
        val srcW = bounds.outWidth
        val srcH = bounds.outHeight
        if (srcW <= 0 || srcH <= 0) return null

        var sampleSize = 1
        while ((srcW / sampleSize) > reqWidth * 2 && (srcH / sampleSize) > reqHeight * 2) {
          sampleSize *= 2
        }
        while (((srcW / sampleSize).toLong() * (srcH / sampleSize).toLong() * 2L) > maxBytes) {
          sampleSize *= 2
        }

        val decodeOpts =
          BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.RGB_565
          }
        BitmapFactory.decodeResource(context.resources, R.drawable.img_duco_card_bg, decodeOpts)
      }.getOrNull()
    }
  }
}

@Composable
fun DucoWidgetContent(
  snapshot: DucoWidgetSnapshot,
  isDark: Boolean,
  size: DpSize = DpSize(280.dp, 120.dp),
  backgroundBitmap: Bitmap? = null,
) {
  val context = LocalContext.current
  val outerBg = if (isDark) Color(0xFF101814) else Color(0xFFFFFFFF)
  val streakPanelBg = if (isDark) Color(0xFF2B2312) else Color(0xFFFEF3C7)
  val streakAccent = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706)
  val streakNumColor = if (isDark) Color(0xFFFDE68A) else Color(0xFF92400E)
  val streakSubColor = if (isDark) Color(0xFFFCD34D) else Color(0xFFB45309)

  // Deep emerald backdrop & crisp light foreground on the Duco background-image panel
  val ducoFallbackBg = Color(0xFF0A331C)
  val ducoScrimColor = if (isDark) Color(0xB8041C0E) else Color(0x9E062916)
  val ducoTitleColor = Color(0xFFFFFFFF)
  val ducoBodyColor = Color(0xFFE6F4EA)
  val countMintColor = Color(0xFF8BD79B)
  val buttonBg = Color(0xFF34D399)
  val buttonText = Color(0xFF00210B)

  val isNarrow = size.width < 220.dp
  val streakPanelWidth = if (isNarrow) 76.dp else 94.dp

  val rootModifier =
    GlanceModifier.fillMaxSize()
      .appWidgetBackground()
      .background(outerBg)
      .let { mod ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          mod.cornerRadius(android.R.dimen.system_app_widget_background_radius)
        } else {
          mod.cornerRadius(22.dp)
        }
      }
      .padding(10.dp)
      .clickable(actionStartActivity<MainActivity>())

  Row(
    modifier = rootModifier,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    // Left Side: Daily Streak
    val streakCd = context.getString(R.string.cd_streak_badge, snapshot.streakDays)
    Column(
      modifier =
        GlanceModifier.width(streakPanelWidth)
          .fillMaxHeight()
          .cornerRadius(16.dp)
          .background(streakPanelBg)
          .padding(horizontal = 6.dp, vertical = 8.dp)
          .semantics {
            testTag = "widget_streak_panel"
            contentDescription = streakCd
          },
      verticalAlignment = Alignment.CenterVertically,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Image(
        provider = ImageProvider(R.drawable.ic_widget_flame),
        contentDescription = null,
        colorFilter = ColorFilter.tint(ColorProvider(streakAccent)),
        modifier = GlanceModifier.size(if (isNarrow) 22.dp else 26.dp),
      )
      Spacer(modifier = GlanceModifier.height(3.dp))
      Text(
        text = "${snapshot.streakDays}",
        style =
          TextStyle(
            color = ColorProvider(streakNumColor),
            fontSize = if (isNarrow) 20.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
          ),
        maxLines = 1,
        modifier = GlanceModifier.semantics { testTag = "widget_streak_count" },
      )
      Text(
        text = context.getString(R.string.widget_streak_label),
        style =
          TextStyle(
            color = ColorProvider(streakSubColor),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
          ),
        maxLines = 1,
      )
    }

    Spacer(modifier = GlanceModifier.width(10.dp))

    // Right Side: Active / Current Duco with Islamic Emerald Background Image
    val activeDuco = snapshot.activeDuco
    Box(
      modifier =
        GlanceModifier.defaultWeight()
          .fillMaxHeight()
          .cornerRadius(16.dp)
          .background(ducoFallbackBg)
          .semantics { testTag = "widget_duco_panel" },
    ) {
      if (backgroundBitmap != null) {
        Image(
          provider = ImageProvider(backgroundBitmap),
          contentDescription = null,
          contentScale = ContentScale.Crop,
          modifier = GlanceModifier.fillMaxSize().cornerRadius(16.dp),
        )
        Box(
          modifier =
            GlanceModifier.fillMaxSize().cornerRadius(16.dp).background(ducoScrimColor),
        ) {}
      }

      Column(
        modifier = GlanceModifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        if (activeDuco == null) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
              provider = ImageProvider(R.drawable.ic_widget_tasbiix),
              contentDescription = null,
              colorFilter = ColorFilter.tint(ColorProvider(countMintColor)),
              modifier = GlanceModifier.size(16.dp),
            )
            Spacer(modifier = GlanceModifier.width(6.dp))
            Text(
              text = context.getString(R.string.app_name),
              style =
                TextStyle(
                  color = ColorProvider(ducoTitleColor),
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                ),
              maxLines = 1,
            )
          }
          Spacer(modifier = GlanceModifier.height(4.dp))
          Text(
            text = context.getString(R.string.widget_empty_title),
            style =
              TextStyle(
                color = ColorProvider(ducoBodyColor),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
              ),
            maxLines = 2,
            modifier = GlanceModifier.semantics { testTag = "widget_empty_text" },
          )
        } else {
          val headerLabel =
            if (snapshot.activeIndex > 0) "#${snapshot.activeIndex} · ${activeDuco.title}"
            else activeDuco.title

          Text(
            text = headerLabel,
            style =
              TextStyle(
                color = ColorProvider(ducoTitleColor),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
              ),
            maxLines = 1,
            modifier = GlanceModifier.fillMaxWidth().semantics { testTag = "widget_duco_title" },
          )

          Spacer(modifier = GlanceModifier.height(3.dp))

          Text(
            text = activeDuco.text,
            style =
              TextStyle(
                color = ColorProvider(ducoBodyColor),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
              ),
            maxLines = if (isNarrow) 1 else 2,
            modifier =
              GlanceModifier.fillMaxWidth().defaultWeight().semantics {
                testTag = "widget_duco_text"
              },
          )

          Spacer(modifier = GlanceModifier.height(4.dp))

          Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            val target = activeDuco.targetCount?.takeIf { it > 0 }
            val countLabel =
              if (target != null) {
                "${activeDuco.count} / $target"
              } else {
                "${activeDuco.count}"
              }
            Text(
              text = countLabel,
              style =
                TextStyle(
                  color = ColorProvider(countMintColor),
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                ),
              maxLines = 1,
              modifier =
                GlanceModifier.defaultWeight().semantics { testTag = "widget_duco_count" },
            )

            Box(
              modifier =
                GlanceModifier.cornerRadius(999.dp)
                  .background(buttonBg)
                  .padding(horizontal = 12.dp, vertical = 5.dp)
                  .clickable(actionRunCallback<IncrementDucoWidgetAction>())
                  .semantics {
                    testTag = "widget_increment_button"
                    contentDescription = context.getString(R.string.widget_tap_count_cd)
                  },
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = context.getString(R.string.widget_plus_one),
                style =
                  TextStyle(
                    color = ColorProvider(buttonText),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                  ),
                maxLines = 1,
              )
            }
          }
        }
      }
    }
  }
}
