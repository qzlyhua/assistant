package cn.qzlyhua.assistant.util.word;

import cn.qzlyhua.assistant.dto.specification.Dictionary;
import cn.qzlyhua.assistant.dto.specification.DictionaryTable;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.Rows;
import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import com.deepoove.poi.policy.TableRenderPolicy;
import com.deepoove.poi.util.TableTools;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.ArrayList;
import java.util.List;

/**
 * 传输规范WORD文件，数据字典Table渲染器
 *
 * @author yanghua
 */
public class CsrDictionariesTablePolicy extends DynamicTableRenderPolicy {
    int dicStartRow = 1;

    @Override
    public void render(XWPFTable table, Object data) throws Exception {
        if (null == data) {
            return;
        }

        List<DictionaryTable> tables = (List<DictionaryTable>) data;
        table.removeRow(dicStartRow);

        for (DictionaryTable t : tables) {
            List<RowRenderData> rows = new ArrayList<>(t.getDictionaryList().size());
            for (Dictionary d : t.getDictionaryList()) {
                rows.add(Rows.of(t.getType(), d.getCode(), d.getName())
                        .center().textFontSize(11).textFontFamily("微软雅黑").create());
            }

            // 循环插入行
            for (int i = rows.size() - 1; i >= 0; i--) {
                XWPFTableRow insertNewTableRow = table.insertNewTableRow(dicStartRow);
                for (int j = 0; j < 3; j++) {
                    insertNewTableRow.createCell();
                }
                TableRenderPolicy.Helper.renderRow(table.getRow(dicStartRow), rows.get(i));
            }

            // 合并单元格
            TableTools.mergeCellsVertically(table, 0, dicStartRow, dicStartRow + t.getSize() - 1);

            // 修改游标位置
            dicStartRow += t.getSize();
        }
    }
}
