/* Dashboard and report charts. Data arrives through data-* attributes so no
   inline script is needed anywhere in the application. */
(function () {
  'use strict';

  function css(name, fallback) {
    var value = getComputedStyle(document.documentElement).getPropertyValue(name).trim();
    return value || fallback;
  }

  function parseList(value) {
    return value ? value.split('|').filter(function (part) { return part.length > 0; }) : [];
  }

  function parseNumbers(value) {
    return parseList(value).map(function (part) { return Number(part) || 0; });
  }

  function onReady(fn) {
    if (document.readyState !== 'loading') {
      fn();
    } else {
      document.addEventListener('DOMContentLoaded', fn);
    }
  }

  onReady(function () {
    if (typeof window.Chart === 'undefined') {
      return;
    }

    var bodyFont = "'Public Sans', sans-serif";
    Chart.defaults.font.family = bodyFont;
    Chart.defaults.color = css('--muted', '#626d7d');
    Chart.defaults.borderColor = css('--rule', '#dce1e9');

    document.querySelectorAll('[data-chart]').forEach(function (canvas) {
      var kind = canvas.getAttribute('data-chart');
      var labels = parseList(canvas.getAttribute('data-labels'));
      var values = parseNumbers(canvas.getAttribute('data-values'));

      if (kind === 'status-doughnut') {
        var modifiers = parseList(canvas.getAttribute('data-modifiers'));
        var colours = modifiers.map(function (modifier) {
          switch (modifier) {
            case 'PENDING': return css('--s-pending', '#8a6116');
            case 'PROCESSING': return css('--s-processing', '#1d4e89');
            case 'READY_FOR_PICKUP': return css('--s-ready', '#1f6247');
            default: return css('--navy', '#17304f');
          }
        });
        new Chart(canvas, {
          type: 'doughnut',
          data: {
            labels: labels,
            datasets: [{ data: values, backgroundColor: colours, borderWidth: 2, borderColor: '#fff' }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '62%',
            plugins: { legend: { display: false } }
          }
        });
        return;
      }

      if (kind === 'type-bar') {
        new Chart(canvas, {
          type: 'bar',
          data: {
            labels: labels,
            datasets: [{
              data: values,
              backgroundColor: css('--navy', '#17304f'),
              borderRadius: 3,
              maxBarThickness: 34
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
              y: { beginAtZero: true, ticks: { precision: 0 }, grid: { drawBorder: false } },
              x: { grid: { display: false } }
            }
          }
        });
        return;
      }

      if (kind === 'volume-line') {
        new Chart(canvas, {
          type: 'line',
          data: {
            labels: labels,
            datasets: [{
              data: values,
              borderColor: css('--navy', '#17304f'),
              backgroundColor: 'rgba(23, 48, 79, .07)',
              fill: true,
              tension: .32,
              pointRadius: 3,
              pointBackgroundColor: '#fff',
              pointBorderColor: css('--navy', '#17304f'),
              pointBorderWidth: 2
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
              y: { beginAtZero: true, ticks: { precision: 0 }, grid: { drawBorder: false } },
              x: { grid: { display: false } }
            }
          }
        });
      }
    });
  });
})();
