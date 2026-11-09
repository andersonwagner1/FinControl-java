(() => {
    "use strict";

    const MONTHS = ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"];
    const COLORS = ["#63c98a", "#7da9ed", "#bd91e8", "#e7ae65", "#e5777e",
        "#5fc4b4", "#d18c6a", "#83a1a1", "#c5c766", "#a38ae3"];
    const TYPE_INCOME = new Set(["C", "TC", "R"]);
    const TYPE_EXPENSE = new Set(["D", "TD", "A"]);
    const REPORT_INCOME = new Set(["CREDITO", "RENDA", "RESGATE", "RENDIMENTO"]);
    const REPORT_EXPENSE = new Set(["DEBITO", "APLICACAO", "MENSAL"]);
    const money = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });
    const number = new Intl.NumberFormat("pt-BR", { maximumFractionDigits: 0 });

    const state = {
        rows: [],
        filtered: [],
        page: 1,
        pageSize: 10,
        sortKey: "date",
        sortDirection: "desc"
    };

    const byId = (id) => document.getElementById(id);
    const field = (row, ...keys) => {
        for (const key of keys) {
            if (row[key] !== undefined && row[key] !== null) return row[key];
            const upper = key.toUpperCase();
            if (row[upper] !== undefined && row[upper] !== null) return row[upper];
        }
        return null;
    };
    const text = (value, fallback = "—") => value === null || value === undefined || String(value).trim() === ""
        ? fallback : String(value).trim();
    const amount = (value) => {
        if (typeof value === "number") return Number.isFinite(value) ? value : 0;
        if (value === null || value === undefined || value === "") return 0;
        const parsed = Number(String(value).trim().replace(/\s/g, "").replace(/\.(?=\d{3}(?:\D|$))/g, "").replace(",", "."));
        return Number.isFinite(parsed) ? parsed : 0;
    };
    const normalize = (value) => String(value ?? "").trim().toUpperCase();
    const dateOf = (row) => {
        const value = field(row, "dtMovimentacao", "DT_MOVIMENTACAO", "date", "data");
        if (value) {
            const date = value instanceof Date ? value
                : /^\d{4}-\d{2}-\d{2}$/.test(String(value))
                    ? new Date(`${value}T00:00:00`)
                    : new Date(value);
            if (!Number.isNaN(date.getTime())) return date;
        }
        const year = Number(field(row, "nrAno", "NR_ANO"));
        const month = Number(field(row, "nrMes", "NR_MES"));
        const day = Number(field(row, "nrDia", "NR_DIA"));
        return year && month ? new Date(year, month - 1, day || 1) : null;
    };
    const dateKey = (date) => date ? `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}` : "";
    const formatDate = (date) => date ? new Intl.DateTimeFormat("pt-BR").format(date) : "—";
    const accountOf = (row) => text(field(row, "bancoConta", "BANCO_CONTA", "dsBancoConta", "descricaoConta", "contaDescricao"));
    const categoryOf = (row) => text(field(row, "dsCategoria", "DS_CATEGORIA", "categoria"));
    const reportOf = (row) => text(field(row, "icRelatorio", "IC_RELATORIO", "tipoRelatorio"));
    const kindOf = (row) => normalize(field(row, "icTipo", "IC_TIPO", "tipo"));
    const creditOf = (row) => amount(field(row, "vlCredito", "VL_CREDITO", "credito"));
    const debitOf = (row) => amount(field(row, "vlDebito", "VL_DEBITO", "debito"));
    const reportTypeOf = (row) => normalize(reportOf(row));
    const isIncome = (row) => TYPE_INCOME.has(kindOf(row))
        || (!TYPE_EXPENSE.has(kindOf(row)) && REPORT_INCOME.has(reportTypeOf(row)));
    const isExpense = (row) => TYPE_EXPENSE.has(kindOf(row))
        || (!TYPE_INCOME.has(kindOf(row)) && REPORT_EXPENSE.has(reportTypeOf(row)));
    const isInvestment = (row) => kindOf(row) === "A" || reportTypeOf(row) === "APLICACAO";
    const isRedemption = (row) => kindOf(row) === "R" || reportTypeOf(row) === "RESGATE";
    const typeLabel = (row) => text(field(row, "dsTipoMovimentacao", "DS_TIPO_MOVIMENTACAO", "tipoMovimentacao", "ds_tipo_movimentacao"));
    const observationOf = (row) => text(field(row, "dsObservacao", "DS_OBSERVACAO", "descricao", "description"));
    const investmentOf = (row) => text(field(row, "dsInvestimento", "DS_INVESTIMENTO", "investimento", "bancoConta", "BANCO_CONTA"));
    const sum = (rows, getter) => rows.reduce((total, row) => total + getter(row), 0);

    function unwrap(payload) {
        if (Array.isArray(payload)) return payload;
        if (payload && Array.isArray(payload.valor)) return payload.valor;
        if (payload && Array.isArray(payload.data)) return payload.data;
        if (payload && Array.isArray(payload.content)) return payload.content;
        if (payload && Array.isArray(payload.movimentacoes)) return payload.movimentacoes;
        throw new Error("O JSON precisa conter uma lista de movimentações.");
    }

    function setStatus(message, isError = false) {
        byId("status-message").textContent = message;
        byId("status-banner").classList.toggle("error", isError);
    }

    function populateFilter(selectId, values, placeholder) {
        const select = byId(selectId);
        const selected = select.value;
        select.replaceChildren(new Option(placeholder, ""));
        [...values].filter(Boolean).sort((a, b) => a.localeCompare(b, "pt-BR", { numeric: true }))
            .forEach((value) => select.add(new Option(value, value)));
        if ([...select.options].some((option) => option.value === selected)) select.value = selected;
    }

    function updateFilters() {
        const months = new Set();
        const years = new Set();
        const accounts = new Set();
        const categories = new Set();
        const reports = new Set();
        state.rows.forEach((row) => {
            const date = dateOf(row);
            if (date) {
                months.add(String(date.getMonth() + 1));
                years.add(String(date.getFullYear()));
            }
            accounts.add(accountOf(row));
            categories.add(categoryOf(row));
            const report = reportOf(row);
            if (report !== "—") reports.add(report);
        });

        const monthSelect = byId("filter-month");
        const selectedMonth = monthSelect.value;
        monthSelect.replaceChildren(new Option("Todos os meses", ""));
        [...months].sort((a, b) => Number(a) - Number(b)).forEach((month) =>
            monthSelect.add(new Option(MONTHS[Number(month) - 1], month)));
        if ([...monthSelect.options].some((option) => option.value === selectedMonth)) monthSelect.value = selectedMonth;

        const yearSelect = byId("filter-year");
        const selectedYear = yearSelect.value;
        yearSelect.replaceChildren(new Option("Todos os anos", ""));
        [...years].sort((a, b) => Number(b) - Number(a)).forEach((year) => yearSelect.add(new Option(year, year)));
        if ([...yearSelect.options].some((option) => option.value === selectedYear)) yearSelect.value = selectedYear;

        populateFilter("filter-account", accounts, "Todas as contas");
        populateFilter("filter-category", categories, "Todas as categorias");
        populateFilter("filter-report", reports, "Todos os tipos");
    }

    function applyFilters() {
        const month = byId("filter-month").value;
        const year = byId("filter-year").value;
        const from = byId("filter-from").value;
        const to = byId("filter-to").value;
        const account = byId("filter-account").value;
        const category = byId("filter-category").value;
        const report = byId("filter-report").value;
        state.filtered = state.rows.filter((row) => {
            const date = dateOf(row);
            const dateString = dateKey(date);
            if (from && (!dateString || dateString < from)) return false;
            if (to && (!dateString || dateString > to)) return false;
            if (!from && !to && month && (!date || String(date.getMonth() + 1) !== month)) return false;
            if (!from && !to && year && (!date || String(date.getFullYear()) !== year)) return false;
            if (account && accountOf(row) !== account) return false;
            if (category && categoryOf(row) !== category) return false;
            if (report && reportOf(row) !== report) return false;
            return true;
        });
        state.page = 1;
        render();
    }

    function renderKpis(rows) {
        const income = sum(rows.filter(isIncome), creditOf);
        const expenses = sum(rows.filter(isExpense), debitOf);
        const invested = sum(rows.filter(isInvestment), debitOf);
        byId("kpi-income").textContent = money.format(income);
        byId("kpi-expenses").textContent = money.format(expenses);
        byId("kpi-net").textContent = money.format(income - expenses);
        byId("kpi-invested").textContent = money.format(invested);
        byId("kpi-net").classList.toggle("credit-value", income - expenses >= 0);
        byId("kpi-net").classList.toggle("debit-value", income - expenses < 0);
    }

    function svgElement(name, attrs = {}) {
        const element = document.createElementNS("http://www.w3.org/2000/svg", name);
        Object.entries(attrs).forEach(([key, value]) => element.setAttribute(key, value));
        return element;
    }

    function renderFlowChart(rows) {
        const container = byId("flow-chart");
        container.replaceChildren();
        if (!rows.length) return showEmpty(container);

        const daily = Boolean(byId("filter-month").value || byId("filter-from").value || byId("filter-to").value);
        const buckets = new Map();
        rows.forEach((row) => {
            const date = dateOf(row);
            if (!date) return;
            const key = daily ? dateKey(date) : `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}`;
            if (!buckets.has(key)) buckets.set(key, { key, date, income: 0, expense: 0 });
            const bucket = buckets.get(key);
            bucket.income += creditOf(row);
            bucket.expense += debitOf(row);
        });
        const data = [...buckets.values()].sort((a, b) => a.key.localeCompare(b.key));
        if (!data.length) return showEmpty(container);

        const width = 620, height = 210, left = 43, right = 8, top = 10, bottom = 28;
        const innerWidth = width - left - right, innerHeight = height - top - bottom;
        const maximum = Math.max(1, ...data.flatMap((d) => [d.income, d.expense]));
        const niceMax = Math.ceil(maximum / 4 / Math.pow(10, Math.max(0, Math.floor(Math.log10(maximum)) - 1)))
            * Math.pow(10, Math.max(0, Math.floor(Math.log10(maximum)) - 1)) * 4;
        const x = (index) => left + (data.length === 1 ? innerWidth / 2 : index * innerWidth / (data.length - 1));
        const y = (value) => top + innerHeight - value / niceMax * innerHeight;
        const linePath = (key) => data.map((item, index) => `${index ? "L" : "M"}${x(index)},${y(item[key])}`).join(" ");
        const areaPath = (key) => `${linePath(key)} L${x(data.length - 1)},${top + innerHeight} L${x(0)},${top + innerHeight} Z`;
        const svg = svgElement("svg", { viewBox: `0 0 ${width} ${height}`, role: "img", "aria-label": "Fluxo de caixa de entradas e saídas" });
        const defs = svgElement("defs");
        [["incomeGradient", "#54bd82"], ["expenseGradient", "#f07f82"]].forEach(([id, color]) => {
            const gradient = svgElement("linearGradient", { id, x1: "0", x2: "0", y1: "0", y2: "1" });
            gradient.append(svgElement("stop", { offset: "0%", "stop-color": color, "stop-opacity": ".2" }));
            gradient.append(svgElement("stop", { offset: "100%", "stop-color": color, "stop-opacity": "0" }));
            defs.append(gradient);
        });
        svg.append(defs);
        for (let tick = 0; tick <= 4; tick++) {
            const value = niceMax * tick / 4;
            const lineY = y(value);
            svg.append(svgElement("line", { x1: left, x2: width - right, y1: lineY, y2: lineY, class: "chart-grid-line" }));
            const label = svgElement("text", { x: left - 8, y: lineY + 3, "text-anchor": "end", class: "chart-label" });
            label.textContent = shortMoney(value);
            svg.append(label);
        }
        svg.append(svgElement("path", { d: areaPath("income"), class: "chart-area-income" }));
        svg.append(svgElement("path", { d: areaPath("expense"), class: "chart-area-expenses" }));
        svg.append(svgElement("path", { d: linePath("income"), class: "chart-line-income" }));
        svg.append(svgElement("path", { d: linePath("expense"), class: "chart-line-expenses" }));
        const step = Math.max(1, Math.ceil(data.length / 6));
        data.forEach((item, index) => {
            if (index % step !== 0 && index !== data.length - 1) return;
            const label = svgElement("text", { x: x(index), y: height - 7, "text-anchor": "middle", class: "chart-label" });
            label.textContent = daily ? String(item.date.getDate()).padStart(2, "0") : MONTHS[item.date.getMonth()].slice(0, 3);
            svg.append(label);
        });
        data.forEach((item, index) => {
            svg.append(svgElement("circle", { cx: x(index), cy: y(item.income), r: 3.2, class: "chart-point-income" }));
            svg.append(svgElement("circle", { cx: x(index), cy: y(item.expense), r: 3.2, class: "chart-point-expenses" }));
        });
        container.append(svg);
    }

    function shortMoney(value) {
        if (value >= 1000000) return `${(value / 1000000).toFixed(1).replace(".", ",")} mi`;
        if (value >= 1000) return `${(value / 1000).toFixed(0)} mil`;
        return number.format(value);
    }

    function showEmpty(container, message = "Sem dados para os filtros selecionados.") {
        const empty = document.createElement("div");
        empty.className = "empty-chart";
        empty.textContent = message;
        container.append(empty);
    }

    function renderCategoryChart(rows) {
        const container = byId("category-chart");
        container.replaceChildren();
        const categories = new Map();
        rows.filter(isExpense).forEach((row) => {
            const key = categoryOf(row);
            categories.set(key, (categories.get(key) || 0) + debitOf(row));
        });
        const data = [...categories.entries()].sort((a, b) => b[1] - a[1]).slice(0, 8);
        const total = data.reduce((acc, item) => acc + item[1], 0);
        if (!total) return showEmpty(container);

        const donut = document.createElement("div");
        donut.className = "donut";
        let current = 0;
        const stops = data.map(([, value], index) => {
            const start = current;
            current += value / total * 100;
            return `${COLORS[index % COLORS.length]} ${start}% ${current}%`;
        });
        donut.style.background = `conic-gradient(${stops.join(", ")})`;
        const center = document.createElement("div");
        center.className = "donut-center";
        const value = document.createElement("strong");
        value.textContent = shortMoney(total);
        const caption = document.createElement("span");
        caption.textContent = "total de saídas";
        center.append(value, caption);
        donut.append(center);

        const legend = document.createElement("div");
        legend.className = "legend-list";
        data.forEach(([name, amountValue], index) => {
            const item = document.createElement("div");
            item.className = "legend-item";
            const dot = document.createElement("i");
            dot.className = "legend-dot";
            dot.style.backgroundColor = COLORS[index % COLORS.length];
            const label = document.createElement("span");
            label.className = "legend-item-name";
            label.textContent = name;
            const amountLabel = document.createElement("span");
            amountLabel.className = "legend-item-value";
            amountLabel.textContent = `${Math.round(amountValue / total * 100)}%`;
            item.append(dot, label, amountLabel);
            legend.append(item);
        });
        container.append(donut, legend);
    }

    function renderInvestmentChart(rows) {
        const container = byId("investment-chart");
        container.replaceChildren();
        const aggregates = new Map();
        rows.filter((row) => isInvestment(row) || isRedemption(row)).forEach((row) => {
            const key = investmentOf(row);
            if (!aggregates.has(key)) aggregates.set(key, { investment: 0, redemption: 0 });
            const item = aggregates.get(key);
            if (isInvestment(row)) item.investment += debitOf(row);
            if (isRedemption(row)) item.redemption += creditOf(row);
        });
        const data = [...aggregates.entries()].sort((a, b) =>
            (b[1].investment + b[1].redemption) - (a[1].investment + a[1].redemption)).slice(0, 6);
        if (!data.length) return showEmpty(container);
        const max = Math.max(1, ...data.flatMap(([, item]) => [item.investment, item.redemption]));
        data.forEach(([name, item]) => {
            const row = document.createElement("div");
            row.className = "bar-row";
            const label = document.createElement("span");
            label.className = "bar-label";
            label.title = name;
            label.textContent = name;
            const pair = document.createElement("div");
            pair.className = "bar-pair";
            [["fill-aporte", item.investment], ["fill-resgate", item.redemption]].forEach(([className, value]) => {
                const track = document.createElement("div");
                track.className = "bar-track";
                const fill = document.createElement("div");
                fill.className = `bar-fill ${className}`;
                fill.style.width = `${Math.max(value ? 1 : 0, value / max * 100)}%`;
                track.append(fill);
                pair.append(track);
            });
            const total = document.createElement("span");
            total.className = "bar-total";
            total.textContent = shortMoney(item.investment + item.redemption);
            row.append(label, pair, total);
            container.append(row);
        });
    }

    function renderIncomeChart(rows) {
        const container = byId("income-chart");
        container.replaceChildren();
        const aggregates = new Map();
        rows.filter(isIncome).forEach((row) => {
            const key = categoryOf(row);
            aggregates.set(key, (aggregates.get(key) || 0) + creditOf(row));
        });
        const data = [...aggregates.entries()].sort((a, b) => b[1] - a[1]).slice(0, 6);
        const max = Math.max(1, ...data.map(([, value]) => value));
        if (!data.length) return showEmpty(container);
        data.forEach(([name, value]) => {
            const row = document.createElement("div");
            row.className = "horizontal-row";
            const label = document.createElement("span");
            label.className = "horizontal-label";
            label.title = name;
            label.textContent = name;
            const track = document.createElement("div");
            track.className = "horizontal-track";
            const fill = document.createElement("div");
            fill.className = "horizontal-fill";
            fill.style.width = `${value / max * 100}%`;
            track.append(fill);
            const amountLabel = document.createElement("span");
            amountLabel.className = "horizontal-value";
            amountLabel.textContent = shortMoney(value);
            row.append(label, track, amountLabel);
            container.append(row);
        });
    }

    function sortedRows(rows) {
        const fieldBySort = {
            date: (row) => dateKey(dateOf(row)),
            description: observationOf,
            category: categoryOf,
            account: accountOf,
            type: typeLabel,
            credit: creditOf,
            debit: debitOf
        };
        const getter = fieldBySort[state.sortKey] || fieldBySort.date;
        return [...rows].sort((a, b) => {
            const first = getter(a), second = getter(b);
            const comparison = typeof first === "number" && typeof second === "number"
                ? first - second
                : String(first).localeCompare(String(second), "pt-BR", { numeric: true });
            return comparison * (state.sortDirection === "asc" ? 1 : -1);
        });
    }

    function renderTable(rows) {
        const body = byId("transactions-body");
        body.replaceChildren();
        const sorted = sortedRows(rows);
        const pageCount = Math.max(1, Math.ceil(sorted.length / state.pageSize));
        state.page = Math.min(state.page, pageCount);
        const start = (state.page - 1) * state.pageSize;
        sorted.slice(start, start + state.pageSize).forEach((row) => {
            const tr = document.createElement("tr");
            const dateCell = document.createElement("td");
            dateCell.className = "cell-date";
            dateCell.textContent = formatDate(dateOf(row));
            const descriptionCell = document.createElement("td");
            descriptionCell.className = "cell-description";
            descriptionCell.title = observationOf(row);
            descriptionCell.textContent = observationOf(row);
            const categoryCell = document.createElement("td");
            categoryCell.className = "cell-muted";
            categoryCell.textContent = categoryOf(row);
            const accountCell = document.createElement("td");
            accountCell.className = "cell-muted";
            accountCell.textContent = accountOf(row);
            const typeCell = document.createElement("td");
            const chip = document.createElement("span");
            chip.className = "type-chip";
            chip.textContent = typeLabel(row);
            typeCell.append(chip);
            const creditCell = document.createElement("td");
            creditCell.className = "align-right credit-value";
            creditCell.textContent = creditOf(row) ? money.format(creditOf(row)) : "—";
            const debitCell = document.createElement("td");
            debitCell.className = "align-right debit-value";
            debitCell.textContent = debitOf(row) ? money.format(debitOf(row)) : "—";
            tr.append(dateCell, descriptionCell, categoryCell, accountCell, typeCell, creditCell, debitCell);
            body.append(tr);
        });
        if (!sorted.length) {
            const row = document.createElement("tr");
            const cell = document.createElement("td");
            cell.colSpan = 7;
            cell.className = "empty-row";
            cell.textContent = state.rows.length ? "Nenhuma movimentação corresponde aos filtros." : "Importe ou conecte seus dados para começar.";
            row.append(cell);
            body.append(row);
        }
        const shownStart = sorted.length ? start + 1 : 0;
        const shownEnd = Math.min(start + state.pageSize, sorted.length);
        byId("results-count").textContent = `${number.format(sorted.length)} ${sorted.length === 1 ? "registro" : "registros"}`;
        byId("page-summary").textContent = `Mostrando ${number.format(shownStart)}–${number.format(shownEnd)} de ${number.format(sorted.length)}`;
        byId("page-number").textContent = `${state.page} / ${pageCount}`;
        byId("previous-page").disabled = state.page <= 1;
        byId("next-page").disabled = state.page >= pageCount;
    }

    function render() {
        renderKpis(state.filtered);
        renderFlowChart(state.filtered);
        renderCategoryChart(state.filtered);
        renderInvestmentChart(state.filtered);
        renderIncomeChart(state.filtered);
        renderTable(state.filtered);
    }

    function setRows(rows, message) {
        state.rows = rows.map((row) => row && typeof row === "object" ? row : {}).filter((row) => Object.keys(row).length);
        updateFilters();
        applyFilters();
        setStatus(message || `${number.format(state.rows.length)} movimentações carregadas.`);
    }

    const demoRows = [
        { ID: 1, BANCO_CONTA: "Itaú · Conta principal", DS_OBSERVACAO: "Salário mensal", DT_MOVIMENTACAO: "2026-11-02", VL_CREDITO: 7800, VL_DEBITO: 0, ds_tipo_movimentacao: "Crédito", IC_RELATORIO: "RENDA", DS_CATEGORIA: "Salário", IC_TIPO: "C", DS_INVESTIMENTO: "" },
        { ID: 2, BANCO_CONTA: "Itaú · Conta principal", DS_OBSERVACAO: "Aluguel do apartamento", DT_MOVIMENTACAO: "2026-11-03", VL_CREDITO: 0, VL_DEBITO: 2300, ds_tipo_movimentacao: "Débito automático", IC_RELATORIO: "MENSAL", DS_CATEGORIA: "Moradia", IC_TIPO: "D", DS_INVESTIMENTO: "" },
        { ID: 3, BANCO_CONTA: "Nubank · Cartão", DS_OBSERVACAO: "Compras do mercado", DT_MOVIMENTACAO: "2026-11-04", VL_CREDITO: 0, VL_DEBITO: 486.75, ds_tipo_movimentacao: "Cartão de crédito", IC_RELATORIO: "DEBITO", DS_CATEGORIA: "Alimentação", IC_TIPO: "TD", DS_INVESTIMENTO: "" },
        { ID: 4, BANCO_CONTA: "Itaú · Conta principal", DS_OBSERVACAO: "Aporte Tesouro Selic", DT_MOVIMENTACAO: "2026-11-05", VL_CREDITO: 0, VL_DEBITO: 1000, ds_tipo_movimentacao: "Aplicação", IC_RELATORIO: "APLICACAO", DS_CATEGORIA: "Investimentos", IC_TIPO: "A", DS_INVESTIMENTO: "Tesouro Selic" },
        { ID: 5, BANCO_CONTA: "Itaú · Conta principal", DS_OBSERVACAO: "Freelance de design", DT_MOVIMENTACAO: "2026-11-08", VL_CREDITO: 1250, VL_DEBITO: 0, ds_tipo_movimentacao: "Transferência recebida", IC_RELATORIO: "RENDA", DS_CATEGORIA: "Renda extra", IC_TIPO: "C", DS_INVESTIMENTO: "" },
        { ID: 6, BANCO_CONTA: "Nubank · Conta", DS_OBSERVACAO: "Conta de energia", DT_MOVIMENTACAO: "2026-11-10", VL_CREDITO: 0, VL_DEBITO: 178.4, ds_tipo_movimentacao: "Débito automático", IC_RELATORIO: "MENSAL", DS_CATEGORIA: "Moradia", IC_TIPO: "D", DS_INVESTIMENTO: "" },
        { ID: 7, BANCO_CONTA: "Itaú · Conta principal", DS_OBSERVACAO: "Rendimento CDB", DT_MOVIMENTACAO: "2026-11-12", VL_CREDITO: 84.32, VL_DEBITO: 0, ds_tipo_movimentacao: "Rendimento", IC_RELATORIO: "RENDIMENTO", DS_CATEGORIA: "Investimentos", IC_TIPO: "R", DS_INVESTIMENTO: "CDB liquidez diária" },
        { ID: 8, BANCO_CONTA: "Nubank · Cartão", DS_OBSERVACAO: "Restaurante", DT_MOVIMENTACAO: "2026-11-14", VL_CREDITO: 0, VL_DEBITO: 124.9, ds_tipo_movimentacao: "Cartão de crédito", IC_RELATORIO: "DEBITO", DS_CATEGORIA: "Alimentação", IC_TIPO: "TD", DS_INVESTIMENTO: "" },
        { ID: 9, BANCO_CONTA: "Itaú · Conta principal", DS_OBSERVACAO: "Resgate de investimento", DT_MOVIMENTACAO: "2026-11-17", VL_CREDITO: 500, VL_DEBITO: 0, ds_tipo_movimentacao: "Resgate", IC_RELATORIO: "RESGATE", DS_CATEGORIA: "Investimentos", IC_TIPO: "R", DS_INVESTIMENTO: "Tesouro Selic" },
        { ID: 10, BANCO_CONTA: "Itaú · Conta principal", DS_OBSERVACAO: "Plano de saúde", DT_MOVIMENTACAO: "2026-11-18", VL_CREDITO: 0, VL_DEBITO: 345, ds_tipo_movimentacao: "Débito automático", IC_RELATORIO: "MENSAL", DS_CATEGORIA: "Saúde", IC_TIPO: "D", DS_INVESTIMENTO: "" },
        { ID: 11, BANCO_CONTA: "Nubank · Cartão", DS_OBSERVACAO: "Cinema e lazer", DT_MOVIMENTACAO: "2026-11-21", VL_CREDITO: 0, VL_DEBITO: 92, ds_tipo_movimentacao: "Cartão de crédito", IC_RELATORIO: "DEBITO", DS_CATEGORIA: "Lazer", IC_TIPO: "TD", DS_INVESTIMENTO: "" },
        { ID: 12, BANCO_CONTA: "Itaú · Conta principal", DS_OBSERVACAO: "Aporte CDB", DT_MOVIMENTACAO: "2026-11-25", VL_CREDITO: 0, VL_DEBITO: 650, ds_tipo_movimentacao: "Aplicação", IC_RELATORIO: "APLICACAO", DS_CATEGORIA: "Investimentos", IC_TIPO: "A", DS_INVESTIMENTO: "CDB liquidez diária" }
    ];

    byId("json-file").addEventListener("change", async (event) => {
        const file = event.target.files?.[0];
        if (!file) return;
        try {
            setRows(unwrap(JSON.parse(await file.text())), `Dados carregados de ${file.name}.`);
        } catch (error) {
            setStatus(`Não foi possível ler o arquivo: ${error.message}`, true);
        } finally {
            event.target.value = "";
        }
    });

    byId("url-toggle").addEventListener("click", () => byId("source-panel").classList.toggle("hidden"));
    byId("load-url").addEventListener("click", async () => {
        const url = byId("source-url").value.trim();
        if (!url) return setStatus("Informe uma URL para carregar o JSON.", true);
        try {
            setStatus("Carregando dados da fonte...");
            const response = await fetch(url, { headers: { Accept: "application/json" } });
            if (!response.ok) throw new Error(`A fonte respondeu com HTTP ${response.status}.`);
            setRows(unwrap(await response.json()), `Dados carregados de ${url}.`);
        } catch (error) {
            setStatus(`Não foi possível carregar a fonte: ${error.message}`, true);
        }
    });
    byId("refresh-button").addEventListener("click", () => {
        if (byId("source-url").value.trim()) byId("load-url").click();
        else byId("json-file").click();
    });
    byId("demo-button").addEventListener("click", () => setRows(demoRows, "Exibindo dados demonstrativos — importe seu JSON para ver seus dados."));
    ["filter-month", "filter-year", "filter-from", "filter-to", "filter-account", "filter-category", "filter-report"]
        .forEach((id) => byId(id).addEventListener("change", applyFilters));
    byId("clear-filters").addEventListener("click", () => {
        ["filter-month", "filter-year", "filter-from", "filter-to", "filter-account", "filter-category", "filter-report"]
            .forEach((id) => { byId(id).value = ""; });
        applyFilters();
    });
    document.querySelectorAll(".sort-button").forEach((button) => {
        button.addEventListener("click", () => {
            const key = button.dataset.sort;
            if (state.sortKey === key) state.sortDirection = state.sortDirection === "asc" ? "desc" : "asc";
            else { state.sortKey = key; state.sortDirection = "asc"; }
            renderTable(state.filtered);
        });
    });
    byId("previous-page").addEventListener("click", () => {
        if (state.page > 1) { state.page--; renderTable(state.filtered); }
    });
    byId("next-page").addEventListener("click", () => {
        const pages = Math.max(1, Math.ceil(state.filtered.length / state.pageSize));
        if (state.page < pages) { state.page++; renderTable(state.filtered); }
    });
    byId("theme-toggle").addEventListener("change", (event) => {
        document.body.classList.toggle("light-theme", event.target.checked);
        byId("theme-label").textContent = event.target.checked ? "Claro" : "Escuro";
        localStorage.setItem("finance-dashboard-theme", event.target.checked ? "light" : "dark");
    });

    const savedTheme = localStorage.getItem("finance-dashboard-theme");
    if (savedTheme === "light") {
        byId("theme-toggle").checked = true;
        document.body.classList.add("light-theme");
        byId("theme-label").textContent = "Claro";
    }
    byId("footer-year").textContent = new Date().getFullYear();
    render();
})();
