package view.util;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;


public class DatePicker extends JPanel {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale PT_BR = new Locale("pt", "BR");

    private final Color BG_DARK;
    private final Color BG_HEADER;
    private final Color ACCENT;
    private final Color ACCENT_HOVER;
    private final Color TEXT_PRIMARY;
    private final Color TEXT_SECONDARY;
    private final Color TEXT_DISABLED;
    private final Color DAY_HOVER_BG;
    private final Color TODAY_RING;
    private final Color BORDER_COLOR;
    private final Color FIELD_BG;

    private LocalDate selectedDate;
    private final LocalDate maxDate;
    private JTextField dateField;
    private JButton calendarButton;
    private JPopupMenu popup;
    private CalendarPanel calendarPanel;

    public DatePicker() {
        this(LocalDate.now());
    }

    public DatePicker(LocalDate initialDate) {
        this.maxDate = LocalDate.now();
        this.selectedDate = initialDate != null && !initialDate.isAfter(maxDate) ? initialDate : maxDate;

        Color panelBg = getUIColor("Panel.background", new Color(60, 63, 65));
        Color textFieldBg = getUIColor("TextField.background", new Color(69, 73, 74));
        Color focusColor = getUIColor("Component.focusColor", new Color(0, 123, 255));
        Color fgColor = getUIColor("TextField.foreground", new Color(187, 187, 187));
        Color borderCol = getUIColor("Component.borderColor", new Color(85, 85, 85));

        this.BG_DARK = panelBg;
        this.BG_HEADER = darker(panelBg, 0.85);
        this.ACCENT = focusColor;
        this.ACCENT_HOVER = brighter(focusColor, 1.25);
        this.TEXT_PRIMARY = fgColor;
        this.TEXT_SECONDARY = new Color(
                (fgColor.getRed() + panelBg.getRed()) / 2,
                (fgColor.getGreen() + panelBg.getGreen()) / 2,
                (fgColor.getBlue() + panelBg.getBlue()) / 2
        );
        this.TEXT_DISABLED = darker(TEXT_SECONDARY, 0.6);
        this.DAY_HOVER_BG = brighter(panelBg, 1.2);
        this.TODAY_RING = new Color(focusColor.getRed(), focusColor.getGreen(), focusColor.getBlue(), 100);
        this.BORDER_COLOR = borderCol;
        this.FIELD_BG = textFieldBg;

        setLayout(new BorderLayout(0, 0));
        setOpaque(false);

        buildUI();
    }

    private static Color getUIColor(String key, Color fallback) {
        Color c = UIManager.getColor(key);
        return c != null ? c : fallback;
    }

    private static Color darker(Color c, double factor) {
        return new Color(
                Math.max((int) (c.getRed() * factor), 0),
                Math.max((int) (c.getGreen() * factor), 0),
                Math.max((int) (c.getBlue() * factor), 0),
                c.getAlpha()
        );
    }

    private static Color brighter(Color c, double factor) {
        return new Color(
                Math.min((int) (c.getRed() * factor), 255),
                Math.min((int) (c.getGreen() * factor), 255),
                Math.min((int) (c.getBlue() * factor), 255),
                c.getAlpha()
        );
    }

    private void buildUI() {
        dateField = new JTextField();
        dateField.setText(selectedDate.format(DISPLAY_FORMAT));
        dateField.setEditable(false);
        dateField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        dateField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dateField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                togglePopup();
            }
        });

        calendarButton = new JButton("📅");
        calendarButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        calendarButton.setPreferredSize(new Dimension(34, 28));
        calendarButton.setFocusPainted(false);
        calendarButton.setBorderPainted(false);
        calendarButton.setContentAreaFilled(false);
        calendarButton.setOpaque(true);
        calendarButton.setBackground(ACCENT);
        calendarButton.setForeground(Color.WHITE);
        calendarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        calendarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                calendarButton.setBackground(ACCENT_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                calendarButton.setBackground(ACCENT);
            }
        });
        calendarButton.addActionListener(e -> togglePopup());

        add(dateField, BorderLayout.CENTER);
        add(calendarButton, BorderLayout.EAST);
    }

    private void togglePopup() {
        if (popup != null && popup.isVisible()) {
            popup.setVisible(false);
            return;
        }
        showPopup();
    }

    private void showPopup() {
        if (popup != null) {
            popup.setVisible(false);
        }

        calendarPanel = new CalendarPanel(selectedDate);
        popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        popup.setBackground(BG_DARK);
        popup.add(calendarPanel);
        popup.show(this, 0, getHeight() + 2);
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public String getText() {
        return selectedDate.format(DISPLAY_FORMAT);
    }

    public void setDate(LocalDate date) {
        if (date != null && !date.isAfter(maxDate)) {
            this.selectedDate = date;
            dateField.setText(date.format(DISPLAY_FORMAT));
        }
    }

    private class CalendarPanel extends JPanel {

        private YearMonth currentMonth;
        private JLabel monthYearLabel;
        private JPanel daysGrid;

        CalendarPanel(LocalDate reference) {
            this.currentMonth = YearMonth.from(reference);
            setBackground(BG_DARK);
            setLayout(new BorderLayout(0, 0));
            setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            setPreferredSize(new Dimension(295, 310));

            add(buildHeader(), BorderLayout.NORTH);
            add(buildBody(), BorderLayout.CENTER);
        }

        private JPanel buildHeader() {
            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(BG_HEADER);
            header.setBorder(BorderFactory.createEmptyBorder(6, 4, 8, 4));

            JButton prevBtn = createNavButton("◀");
            JButton nextBtn = createNavButton("▶");

            monthYearLabel = new JLabel("", JLabel.CENTER);
            monthYearLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            monthYearLabel.setForeground(TEXT_PRIMARY);
            updateMonthYearLabel();

            prevBtn.addActionListener(e -> {
                currentMonth = currentMonth.minusMonths(1);
                updateMonthYearLabel();
                refreshDays();
            });

            nextBtn.addActionListener(e -> {
                YearMonth maxMonth = YearMonth.from(maxDate);
                if (currentMonth.isBefore(maxMonth)) {
                    currentMonth = currentMonth.plusMonths(1);
                    updateMonthYearLabel();
                    refreshDays();
                }
            });

            header.add(prevBtn, BorderLayout.WEST);
            header.add(monthYearLabel, BorderLayout.CENTER);
            header.add(nextBtn, BorderLayout.EAST);

            return header;
        }

        private JPanel buildBody() {
            JPanel body = new JPanel(new BorderLayout(0, 4));
            body.setBackground(BG_DARK);

            JPanel weekHeader = new JPanel(new GridLayout(1, 7, 2, 0));
            weekHeader.setBackground(BG_DARK);
            weekHeader.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

            DayOfWeek[] daysOfWeek = {
                    DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY
            };

            for (DayOfWeek dow : daysOfWeek) {
                String name = dow.getDisplayName(TextStyle.SHORT, PT_BR).toUpperCase();
                if (name.length() > 3) name = name.substring(0, 3);
                JLabel lbl = new JLabel(name, JLabel.CENTER);
                lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
                lbl.setForeground(TEXT_SECONDARY);
                weekHeader.add(lbl);
            }

            body.add(weekHeader, BorderLayout.NORTH);

            daysGrid = new JPanel(new GridLayout(6, 7, 2, 2));
            daysGrid.setBackground(BG_DARK);
            populateDays();

            body.add(daysGrid, BorderLayout.CENTER);
            return body;
        }

        private void updateMonthYearLabel() {
            String month = currentMonth.getMonth().getDisplayName(TextStyle.FULL, PT_BR);
            month = month.substring(0, 1).toUpperCase() + month.substring(1);
            monthYearLabel.setText(month + " " + currentMonth.getYear());
        }

        private void refreshDays() {
            daysGrid.removeAll();
            populateDays();
            daysGrid.revalidate();
            daysGrid.repaint();
        }

        private void populateDays() {
            LocalDate first = currentMonth.atDay(1);
            int startOffset = first.getDayOfWeek().getValue() % 7; // DOM=0, SEG=1, ...

            LocalDate today = LocalDate.now();

            for (int i = 0; i < startOffset; i++) {
                daysGrid.add(createEmptyCell());
            }

            int daysInMonth = currentMonth.lengthOfMonth();
            for (int day = 1; day <= daysInMonth; day++) {
                LocalDate date = currentMonth.atDay(day);
                boolean isFuture = date.isAfter(maxDate);
                boolean isToday = date.equals(today);
                boolean isSelected = date.equals(selectedDate);

                daysGrid.add(createDayCell(day, date, isFuture, isToday, isSelected));
            }

            int totalCells = startOffset + daysInMonth;
            for (int i = totalCells; i < 42; i++) {
                daysGrid.add(createEmptyCell());
            }
        }

        private JLabel createEmptyCell() {
            JLabel lbl = new JLabel("");
            lbl.setOpaque(true);
            lbl.setBackground(BG_DARK);
            return lbl;
        }

        private JLabel createDayCell(int day, LocalDate date, boolean isFuture, boolean isToday, boolean isSelected) {
            JLabel lbl = new JLabel(String.valueOf(day), JLabel.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();
                    int size = Math.min(w, h) - 4;
                    int x = (w - size) / 2;
                    int y = (h - size) / 2;

                    if (isSelected && !isFuture) {
                        g2.setColor(ACCENT);
                        g2.fill(new RoundRectangle2D.Float(x, y, size, size, size, size));
                    } else if (getBackground().equals(DAY_HOVER_BG) && !isFuture) {
                        g2.setColor(DAY_HOVER_BG);
                        g2.fill(new RoundRectangle2D.Float(x, y, size, size, size, size));
                    }

                    if (isToday && !isSelected) {
                        g2.setColor(TODAY_RING);
                        g2.setStroke(new BasicStroke(2f));
                        g2.draw(new RoundRectangle2D.Float(x + 1, y + 1, size - 2, size - 2, size - 2, size - 2));
                    }

                    g2.dispose();
                    super.paintComponent(g);
                }
            };

            lbl.setOpaque(false);
            lbl.setPreferredSize(new Dimension(36, 32));
            lbl.setFont(new Font("SansSerif", isToday ? Font.BOLD : Font.PLAIN, 13));

            if (isFuture) {
                lbl.setForeground(TEXT_DISABLED);
                lbl.setCursor(Cursor.getDefaultCursor());
                lbl.setToolTipText("Data futura não permitida");
            } else if (isSelected) {
                lbl.setForeground(Color.WHITE);
                lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                lbl.setForeground(TEXT_PRIMARY);
                lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                lbl.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        lbl.setBackground(DAY_HOVER_BG);
                        lbl.repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        lbl.setBackground(BG_DARK);
                        lbl.repaint();
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selectedDate = date;
                        dateField.setText(date.format(DISPLAY_FORMAT));
                        if (popup != null) {
                            popup.setVisible(false);
                        }
                    }
                });
            }

            return lbl;
        }

        private JButton createNavButton(String text) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));
            btn.setPreferredSize(new Dimension(36, 28));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
            btn.setBackground(BG_HEADER);
            btn.setForeground(TEXT_PRIMARY);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(DAY_HOVER_BG);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(BG_HEADER);
                }
            });
            return btn;
        }
    }


    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, 4, 2, 4);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(2, 4, 2, 4);
            return insets;
        }
    }
}
