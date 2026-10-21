/* School Documents Request System — interface behaviour.
   No inline scripts: everything is wired from these delegated listeners. */
(function () {
  'use strict';

  function onReady(fn) {
    if (document.readyState !== 'loading') {
      fn();
    } else {
      document.addEventListener('DOMContentLoaded', fn);
    }
  }

  onReady(function () {
    wirePasswordToggles();
    wireSubmitLoading();
    wireConfirmations();
    wireMobileNav();
    wireAutoSubmitFilters();
    wireUnreadPolling();
    wirePrint();
  });

  /* Print the current record --------------------------------------------- */
  function wirePrint() {
    document.querySelectorAll('[data-print]').forEach(function (button) {
      button.addEventListener('click', function (event) {
        event.preventDefault();
        window.print();
      });
    });
  }

  /* Show / hide password -------------------------------------------------- */
  function wirePasswordToggles() {
    document.querySelectorAll('[data-toggle-password]').forEach(function (button) {
      button.addEventListener('click', function () {
        var target = document.getElementById(button.getAttribute('data-toggle-password'));
        if (!target) {
          return;
        }
        var revealing = target.type === 'password';
        target.type = revealing ? 'text' : 'password';
        var icon = button.querySelector('i');
        if (icon) {
          icon.className = revealing ? 'bi bi-eye-slash' : 'bi bi-eye';
        }
        button.setAttribute('aria-label', revealing ? 'Hide password' : 'Show password');
        button.setAttribute('aria-pressed', String(revealing));
      });
    });
  }

  /* Loading feedback + double-submit guard -------------------------------- */
  function wireSubmitLoading() {
    document.querySelectorAll('form').forEach(function (form) {
      form.addEventListener('submit', function (event) {
        if (form.dataset.skipLoading === 'true') {
          return;
        }
        var button = event.submitter || form.querySelector('button[type="submit"]');
        if (!button || button.disabled) {
          return;
        }
        if (button.dataset.confirm && !window.confirm(button.dataset.confirm)) {
          event.preventDefault();
          return;
        }
        if (!form.checkValidity()) {
          return;
        }
        window.setTimeout(function () {
          if (!event.defaultPrevented) {
            button.classList.add('is-loading');
            button.disabled = true;
            if (!button.querySelector('.spinner')) {
              var spinner = document.createElement('span');
              spinner.className = 'spinner';
              spinner.setAttribute('aria-hidden', 'true');
              button.prepend(spinner);
            }
          }
        }, 30);
      });
    });
  }

  /* Confirm consequential actions ---------------------------------------- */
  function wireConfirmations() {
    document.querySelectorAll('[data-confirm]:not([type="submit"])').forEach(function (element) {
      element.addEventListener('click', function (event) {
        if (!window.confirm(element.getAttribute('data-confirm'))) {
          event.preventDefault();
        }
      });
    });
    document.querySelectorAll('button[type="submit"][data-confirm]').forEach(function (button) {
      button.addEventListener('click', function (event) {
        if (!window.confirm(button.getAttribute('data-confirm'))) {
          event.preventDefault();
        }
      });
    });
  }

  /* Mobile navigation ----------------------------------------------------- */
  function wireMobileNav() {
    var sidebar = document.querySelector('.sidebar');
    var toggle = document.querySelector('[data-nav-toggle]');
    if (!sidebar || !toggle) {
      return;
    }
    toggle.addEventListener('click', function () {
      sidebar.classList.toggle('is-open');
      toggle.setAttribute('aria-expanded', String(sidebar.classList.contains('is-open')));
    });
    document.addEventListener('click', function (event) {
      if (!sidebar.classList.contains('is-open')) {
        return;
      }
      if (!sidebar.contains(event.target) && !toggle.contains(event.target)) {
        sidebar.classList.remove('is-open');
        toggle.setAttribute('aria-expanded', 'false');
      }
    });
    document.addEventListener('keydown', function (event) {
      if (event.key === 'Escape' && sidebar.classList.contains('is-open')) {
        sidebar.classList.remove('is-open');
        toggle.setAttribute('aria-expanded', 'false');
        toggle.focus();
      }
    });
  }

  /* Filters that apply on change ---------------------------------------- */
  function wireAutoSubmitFilters() {
    document.querySelectorAll('[data-auto-submit]').forEach(function (element) {
      element.addEventListener('change', function () {
        var form = element.closest('form');
        if (form) {
          form.submit();
        }
      });
    });
  }

  /* Unread notification badge, refreshed once a minute ------------------ */
  function wireUnreadPolling() {
    var badge = document.querySelector('[data-unread-badge]');
    if (!badge || !window.fetch) {
      return;
    }

    function refresh() {
      if (document.visibilityState === 'hidden') {
        return;
      }
      window.fetch(badge.getAttribute('data-endpoint') || '/notifications/unread-count', {
        headers: { 'Accept': 'application/json' },
        credentials: 'same-origin'
      }).then(function (response) {
        return response.ok ? response.json() : null;
      }).then(function (payload) {
        if (!payload) {
          return;
        }
        var count = payload.count || 0;
        badge.textContent = count > 99 ? '99+' : String(count);
        badge.hidden = count === 0;
        badge.closest('.icon-btn').setAttribute('aria-label',
          count === 0 ? 'Notifications, none unread' : 'Notifications, ' + count + ' unread');
      }).catch(function () {
        /* a dropped poll is not worth interrupting anyone */
      });
    }

    refresh();
    window.setInterval(refresh, 60000);
    document.addEventListener('visibilitychange', refresh);
  }
})();
