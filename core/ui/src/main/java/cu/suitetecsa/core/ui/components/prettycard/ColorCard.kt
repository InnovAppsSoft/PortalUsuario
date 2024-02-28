package cu.suitetecsa.core.ui.components.prettycard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ColorCard(
    modifier: Modifier = Modifier,
    detailIcon: ImageVector,
    detailName: String,
    backgroundColor: Color
) {
    PrettyCard(
        modifier = modifier,
        backgroundColor = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = detailIcon,
                contentDescription = detailName,
                modifier = Modifier
                    .padding(top = 8.dp, end = 8.dp)
                    .fillMaxWidth()
                    .wrapContentSize(align = Alignment.TopEnd),
                tint = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = detailName,
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 8.dp)
                    .fillMaxWidth()
                    .wrapContentSize(align = Alignment.BottomStart),
                color = Color.White,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
