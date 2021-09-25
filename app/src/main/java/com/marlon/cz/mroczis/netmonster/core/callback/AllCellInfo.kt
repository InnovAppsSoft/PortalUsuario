package com.marlon.cz.mroczis.netmonster.core.callback

import com.marlon.cz.mroczis.netmonster.core.model.cell.ICell
import com.marlon.cz.mroczis.netmonster.core.model.model.CellError

typealias CellCallbackSuccess = (cells: List<ICell>) -> Unit
typealias CellCallbackError = (error: CellError) -> Unit
