package cu.suitetecsa.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import cu.suitetecsa.core.ui.R
import cu.suitetecsa.core.ui.components.prettycard.PrettyCard
import cu.suitetecsa.core.ui.theme.SuitEtecsaTheme
import cu.suitetecsa.core.ui.theme.VibrantGreen
import cu.suitetecsa.core.ui.theme.VividRed

@Composable
fun ResultDialog(
    isErrorMessage: Boolean = false,
    message: String = "",
    onDismiss: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        PrettyCard {
            ConstraintLayout {
                val (colorIndicator, information) = createRefs()

                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .background(if (!isErrorMessage) VibrantGreen else VividRed)
                        .constrainAs(colorIndicator) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(information.start)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                        }
                ) {}

                Column(
                    Modifier
                        .padding(8.dp)
                        .constrainAs(information) {
                            top.linkTo(parent.top)
                            start.linkTo(colorIndicator.end)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                ) {
                    Text(text = message)
                    TextButton(
                        modifier = Modifier.align(Alignment.End),
                        onClick = onDismiss
                    ) {
                        Text(text = stringResource(R.string.ok))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ResultDialogPreview() {
    SuitEtecsaTheme {
        ResultDialog(message = "Su solicitud esta siendo procesada.")
    }
}
