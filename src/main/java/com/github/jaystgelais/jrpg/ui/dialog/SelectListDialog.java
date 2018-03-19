package com.github.jaystgelais.jrpg.ui.dialog;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.github.jaystgelais.jrpg.input.InputService;
import com.github.jaystgelais.jrpg.ui.Panel;
import com.github.jaystgelais.jrpg.ui.ScreenRegion;
import com.github.jaystgelais.jrpg.ui.SelectItem;
import com.github.jaystgelais.jrpg.ui.SelectList;
import com.github.jaystgelais.jrpg.ui.UiStyle;

import java.util.List;
import java.util.stream.Collectors;

public final class SelectListDialog<T> extends Dialog<T> {
    private final SelectList selectList;

    public SelectListDialog(final List<T> items, final int columns, final ScreenRegion region,
                            final SelectItemRenderer<T> selectItemRenderer, final Listener<T> listener) {
        super(listener, region);
        this.selectList = new SelectList(
                items.stream()
                        .map(item -> new SelectItem<>(selectItemRenderer.render(item), () -> makeSelection(item)))
                        .collect(Collectors.toList()),
                columns
        );
    }

    @Override
    public void handleInput(final InputService inputService) {
        selectList.handleInput(inputService);
    }

    @Override
    protected WidgetGroup createLayout() {
        final Panel<SelectList> panel = new Panel<>(
                selectList,
                UiStyle.get("select-dialog", Panel.PanelStyle.class)
        );

        return panel;
    }

    public interface SelectItemRenderer<T> {
        Actor render(T item);
    }
}
