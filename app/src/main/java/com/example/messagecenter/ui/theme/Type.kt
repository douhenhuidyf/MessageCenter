package com.example.messagecenter.ui.theme.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.messagecenter.R


val HanSans = FontFamily(
    Font(R.font.hansanssc_bold, FontWeight.Bold),
    Font(R.font.hansanssc_light, FontWeight.Light),
    Font(R.font.hansanssc_medium, FontWeight.Medium),
    Font(R.font.hansanssc_normal, FontWeight.Normal)
)
// 创建使用思源黑体的 Typography
val HanSansTypography = Typography(
    displayMedium = TextStyle(
        fontFamily = HanSans,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = HanSans,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = HanSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = HanSans,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp
    )
)

// Set of Material typography styles to start with
//val Typography = Typography(
//    titleLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 22.sp,
//        lineHeight = 28.sp,
//        letterSpacing = 0.sp
//    ),
//    labelSmall = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp,
//        lineHeight = 16.sp,
//        letterSpacing = 0.5.sp
//    )
//)