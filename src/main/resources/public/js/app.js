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

};