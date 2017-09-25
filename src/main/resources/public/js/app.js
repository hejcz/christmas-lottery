window.onload = () => {

    function orderWishes(event) {
        $("#my-wishes").find("tbody")
            .find("tr")
            .each((trIndex, tr) => {
                $(tr).find(".wish-text > input")
                    .each((tdIndex, tdDom) => {
                            let td = $(tdDom);
                            td.prop("name", td.prop("name").replace(/\[\d+]/, `[${trIndex}]`));
                        })
            });
    }

    function assignEditWish() {
        let editBtns = $(".edit-wish");
        editBtns.off('click');
        editBtns.click(e => {
            $(e.target).closest("tr").find(".wish-text input").prop('readonly', (i, v) => !v);
        });
    }

    function assignRemoveWish() {
        let removeBtns = $(".remove-wish");
        removeBtns.off('click');
        removeBtns.click(e => {
            $(e.target).closest("tr").remove();
            orderWishes(e);
        });
    }

    function assignEditRemove() {
        assignEditWish();
        assignRemoveWish();
    }

    assignEditRemove();

    $(".add-wish").click(e => {
        let appendedTr = $("#my-wishes").find("tbody").append(`
        <tr>
            <td class="wish-text">
                <input type="text" class="form-control" name="wishes[0].text" 
                    placeholder="Mam nadzieję, że pod choinką będzie..."/>
            </td>
            <td>
                <button type="button" class="selected-elem btn rank-third rank">TROCHĘ</button>
            </td>
            <td>
                <div class="float-right">
                    <button type="button" class="btn btn-info edit-wish">Edytuj</button>
                    <button type="button" class="btn btn-danger remove-wish">Usuń</button>
                </div>
            </td>
        </tr>`);
        $(appendedTr).find("input").focus();
        assignEditRemove();
        orderWishes();
    });

    function setupRankSelector() {
        $(".selected-elem")
            .off("click")
            .on("click", e => {
                let selectedRank = $(e.target);
                selectedRank.css("opacity", "0.1");
                let menu = selectedRank.parent().find('.rank-select');
                menu.slideToggle(300);

                menu.find("button").off('click');
                menu.find("button").on('click', e => {
                    menu.slideToggle({
                        duration: 200,
                        complete: () => {
                            menu.find("button").off('click');
                            let newRank = $(e.target).clone();
                            selectedRank.replaceWith(newRank);
                            newRank.addClass('selected-elem');
                            setupRankSelector();
                        }
                    });
                });
        });
    }

    setupRankSelector();
};